package ch.bedag.a6z.sipvalidator.validation.module2.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import ch.bedag.a6z.sipvalidator.exception.module2.Validation2bChecksumException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module2.Validation2bChecksumModule;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

public class Validation2bChecksumModuleImpl extends ValidationModuleImpl implements Validation2bChecksumModule {

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(File sipDatei) throws Validation2bChecksumException {

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
                    MessageDigest digest = MessageDigest.getInstance("MD5");
                    EntryInputStream eis = zipfile.openEntryInputStream(fileEntry.getName());
                    BufferedInputStream is = new BufferedInputStream(eis);
                    
                    
                    byte[] buffer = new byte[8192];
                    int read = 0;
                    try {
                        while ((read = is.read(buffer)) > 0) {
                            digest.update(buffer, 0, read);
                        }

                        byte[] md5sum = digest.digest();
                        
                        BigInteger bigInt = new BigInteger(1, md5sum);
                        String output = bigInt.toString(16);

                        while(output.length() < 32 ){
                            output = "0" + output;
                        }

                        String fileName = fileEntry.getName();
                        String toReplace = toplevelDir + "/";
                        fileName = fileName.replace(toReplace, "");
                        
                        filesInSipFile.put(fileName, output);
                        
                        //filesInSipFile.put(fileEntry.getName(), output);

                    } catch (IOException e) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(ERROR_MODULE_BB_CANNOTPROCESSMD5));                                
                        return false;

                    } finally {
                        try {
                            eis.close();
                            is.close();
                        } catch (IOException e) {
                            getMessageService().logError(
                                    getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
                                    getTextResourceService().getText(MESSAGE_DASHES) + 
                                    getTextResourceService().getText(ERROR_MODULE_BB_CANNOTCLOSESTREAMMD5));                                
                            return false;
                        }
                    }
                }
            }

            // keine metadata.xml in der SIP-Datei gefunden
            if (metadataxml == null) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
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
                doc.normalize();
                NodeList nodeLst = doc.getElementsByTagName("datei");

                for (int s = 0; s < nodeLst.getLength(); s++) {
                    Node dateiNode = nodeLst.item(s);
                    NodeIterator nl = XPathAPI.selectNodeIterator(dateiNode, "name");
                    Node nameNode = nl.nextNode();
                    String path = nameNode.getTextContent();
                    
                    NodeIterator nl2 = XPathAPI.selectNodeIterator(dateiNode, "pruefsumme");
                    Node pruefsummeNode = nl2.nextNode();                    
                    String pruefsumme = pruefsummeNode.getTextContent();
                    
                    boolean topReached = false;

                    while (!topReached) {

                        Node parentNode = dateiNode.getParentNode();
                        if (parentNode.getNodeName().equals("inhaltsverzeichnis")) {
                            topReached = true;
                            break;
                        }

                        NodeList childrenNodes = parentNode.getChildNodes();
                        for (int x = 0; x < childrenNodes.getLength(); x++) {
                            Node childNode = childrenNodes.item(x);

                            if (childNode.getNodeName().equals("name")) {
                                path = childNode.getTextContent() + "/" + path;
                                if (dateiNode.getParentNode() != null) {
                                    dateiNode = dateiNode.getParentNode();
                                }
                                break;
                            }
                        }
                    }

                    filesInMetadata.put(path, pruefsumme);
                    path = "";
                    pruefsumme = "";

                }
            } catch (Exception e) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        e.getMessage());                                
                return false;
            }

            // Vergleiche die Prüfsummen aus den Metadaten mit denen aus dem SIP-File
            Set<String> keys = filesInMetadata.keySet();
            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
                String keyMetadata = iterator.next();                
                String pruefsummeMetadata = filesInMetadata.get(keyMetadata);
                String pruefsummeSip = filesInSipFile.get(keyMetadata);

                if (pruefsummeSip == null) {
                    // Die Datei wird im metadata.xml aufgeführt, befindet sich aber nicht in der SIP Datei:
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_BB_MISSINGINSIP, keyMetadata));
                } else {
                    if (!pruefsummeSip.equals(pruefsummeMetadata)) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + keyMetadata);
                        valid = false;
                    }
                }
            }

            zipfile.close();
            is.close();

        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                                
            return false;
        }

        return valid;

    }

    
}
