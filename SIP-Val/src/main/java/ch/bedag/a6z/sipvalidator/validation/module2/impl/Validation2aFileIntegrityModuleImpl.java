package ch.bedag.a6z.sipvalidator.validation.module2.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.bedag.a6z.sipvalidator.exception.module2.Validation2aFileIntegrityException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module2.Validation2aFileIntegrityModule;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

public class Validation2aFileIntegrityModuleImpl extends ValidationModuleImpl implements
        Validation2aFileIntegrityModule {

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(File sipDatei) throws Validation2aFileIntegrityException {

        String toplevelDir = sipDatei.getName();
        int lastDotIdx = toplevelDir.lastIndexOf(".");
        toplevelDir = toplevelDir.substring(0, lastDotIdx);

        boolean valid = true;
        FileEntry metadataxml = null;
        Map<String, String> filesInSipFile = new HashMap<String, String>();
        Map<String, String> filesInMetadata = new HashMap<String, String>();
        
        
        try {
            Zip64File zipfile = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
                
                if (fileEntry.getName().equals("header/" + METADATA) || 
                    fileEntry.getName().equals(toplevelDir + "/header/" + METADATA)) {
                    
                    metadataxml = fileEntry;
                }
                
                if (!fileEntry.isDirectory()) {
                    
                    String[] pathElements = fileEntry.getName().split("/");
                    String filename = pathElements[pathElements.length - 1];
                
                    filesInSipFile.put(filename, filename);
                }

            }

            
            // keine metadata.xml in der SIP-Datei gefunden
            if (metadataxml == null) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Ba) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        getTextResourceService().getText(ERROR_MODULE_AE_NOMETADATAFOUND));                                
                return false;

            }
            
            EntryInputStream eis = zipfile.openEntryInputStream(metadataxml.getName());
            BufferedInputStream is = new BufferedInputStream(eis);

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(is);
                
                NodeList nodeLst = doc.getElementsByTagName("datei");
                
                for (int s = 0; s < nodeLst.getLength(); s++) {
                    Node fstNode = nodeLst.item(s);
                    
                    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                        
                        Element fstElmnt = (Element) fstNode;                        
                        NodeList nodeLst2 = fstElmnt.getElementsByTagName("name");
                        
                        for (int x = 0; x < nodeLst2.getLength(); x++) {
                            Node nameNode = nodeLst2.item(x);                            
                            Element nameElmnt = (Element) nameNode;
                            filesInMetadata.put(nameElmnt.getTextContent(), nameElmnt.getTextContent());
                        }
                    }
                }
                
                Set<String> keysInMetadata = filesInMetadata.keySet();
                for (Iterator<String> iterator = keysInMetadata.iterator(); iterator.hasNext();) {
                    String keyInMetadata = iterator.next();
                    if (!filesInSipFile.containsKey(keyInMetadata)) {
                        
                        // TODO: der TextResourceService scheint in einem eigenen Thread zu laufen!!!
                        // Die Ausgaben im Log sind jedenfalls nicht synchron.
                       
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Ba) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + keyInMetadata);

                        valid = false;
                    } else {
                        filesInSipFile.remove(keyInMetadata);
                    }
                }
                
            } catch (Exception e) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Ba) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        e.getMessage());                                
                return false;
            }

            zipfile.close();
            is.close();
            
        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Ba) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                                
            return false;
        }

        return valid;
    }

}
