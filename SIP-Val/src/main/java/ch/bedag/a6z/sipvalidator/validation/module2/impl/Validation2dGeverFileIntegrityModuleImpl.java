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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.bedag.a6z.sipvalidator.exception.module2.Validation2dGeverFileIntegrityException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module2.Validation2dGeverFileIntegrityModule;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

public class Validation2dGeverFileIntegrityModuleImpl extends ValidationModuleImpl implements Validation2dGeverFileIntegrityModule {

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(File sipDatei) throws Validation2dGeverFileIntegrityException {
        Map<String, String> dateiRefContent = new HashMap<String, String>();
        Map<String, String> dateiRefOrdnungssystem = new HashMap<String, String>();

        String toplevelDir = sipDatei.getName();
        int lastDotIdx = toplevelDir.lastIndexOf(".");
        toplevelDir = toplevelDir.substring(0, lastDotIdx);

        FileEntry metadataxml = null;        
        boolean valid = true;
        
        try {
            Zip64File zipfile = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
                if (fileEntry.getName().equals("header/" + METADATA) || 
                        fileEntry.getName().equals(toplevelDir + "/header/" + METADATA)) {
                    metadataxml = fileEntry;
                    break;
                }
            }

            // keine metadata.xml in der SIP-Datei gefunden
            if (metadataxml == null) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
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
                doc.getDocumentElement().normalize();
                
                XPath xpath = XPathFactory.newInstance().newXPath();
                Element elementName = (Element)xpath.evaluate("/paket/ablieferung", doc, XPathConstants.NODE);
            
                if (elementName == null) {
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_AE_ABLIEFERUNGSTYPUNDEFINED));                                
                    return false;
                }
                
                if (elementName.getAttribute("xsi:type").equals("ablieferungGeverSIP")) {                    
                    
                    NodeList nodeLst = doc.getElementsByTagName("dateiRef");
                    
                    for (int s = 0; s < nodeLst.getLength(); s++) {
                        Node fstNode = nodeLst.item(s);
                        
                        Element fstElement = (Element)fstNode;
                        Node parentNode = fstElement.getParentNode();
                        Element parentElement = (Element)parentNode;
                        NodeList titelList = parentElement.getElementsByTagName("titel");
                        
                        Node titelNode = titelList.item(0);
                        dateiRefOrdnungssystem.put(fstNode.getTextContent(), titelNode.getTextContent());
                    }

                    
                    // alle datei ids aus header/content holen
                    NodeList nameNodes = (NodeList)xpath.evaluate("//ordner/name", doc, XPathConstants.NODESET);
                    for (int s = 0; s < nameNodes.getLength(); s++) {
                        Node dateiNode = nameNodes.item(s);
                        if (dateiNode.getTextContent().equals("content")) {
                            Element dateiElement = (Element) dateiNode;
                            Element parentElement = (Element) dateiElement.getParentNode();
                            
                            NodeList dateiNodes = parentElement.getElementsByTagName("datei");
                            for (int x = 0; x < dateiNodes.getLength(); x++) {
                                Node dateiNode2 = dateiNodes.item(x);
                                Node id = dateiNode2.getAttributes().getNamedItem("id");
                                
                                Element dateiElement2 = (Element)dateiNode2;
                                NodeList nameList = dateiElement2.getElementsByTagName("name");
                                Node titelNode = nameList.item(0);
                                
                                Node dateiParentNode = dateiElement2.getParentNode();
                                Element dateiParentElement = (Element)dateiParentNode;
                                NodeList nameNodes2 = dateiParentElement.getElementsByTagName("name");
                                Node contentName = nameNodes2.item(0);
                                
                                dateiRefContent.put(
                                        id.getNodeValue(), 
                                        "content/"+ contentName.getTextContent() + "/" + titelNode.getTextContent());
                            }
                        }
                    }
                    
                    Set<String> keysContent = dateiRefContent.keySet();
                    boolean titlePrinted = false;
                    for (Iterator<String> iterator = keysContent.iterator(); iterator.hasNext();) {
                        String keyContent = iterator.next();
                        String deleted = dateiRefOrdnungssystem.remove(keyContent);
                        if (deleted == null) {
                            if (!titlePrinted) {
                                getMessageService().logError(
                                        getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                                        getTextResourceService().getText(MESSAGE_DASHES) + 
                                        getTextResourceService().getText(MESSAGE_MODULE_BD_MISSINGINABLIEFERUNG));
                                titlePrinted = true;
                            }                         
                            // Die folgende DateiRef ist vorhanden in metadata/paket/inhaltsverzeichnis/content
                            // aber nicht in metadata/paket/ablieferung/ordnungssystem
                            getMessageService().logError(
                                    getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                                    getTextResourceService().getText(MESSAGE_INDENT) + 
                                    keyContent + 
                                    getTextResourceService().getText(MESSAGE_SLASH) + 
                                    dateiRefContent.get(keyContent));
                            valid = false;
                        }
                    }
                    
                    Set<String> keysRefOrd = dateiRefOrdnungssystem.keySet();
                    if (!keysRefOrd.isEmpty()) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(MESSAGE_MODULE_BD_MISSINGINCONTENT));
                    }
                    for (Iterator<String> iterator = keysRefOrd.iterator(); iterator.hasNext();) {
                        String keyOrd = iterator.next();
                        // Die folgende DateiRef vorhanden in metadata/paket/ablieferung/ordnungssystem, 
                        // aber nicht in metadata/paket/inhaltsverzeichnis/content
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                                getTextResourceService().getText(MESSAGE_INDENT) + 
                                keyOrd + 
                                getTextResourceService().getText(MESSAGE_SLASH) + 
                                dateiRefOrdnungssystem.get(keyOrd));
                        valid = false;
                    }
                    
                    
                } else if (elementName.getAttribute("xsi:type").equals("ablieferungFilesSIP")) {
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(MESSAGE_MODULE_AE_ABLIEFERUNGSTYPFILE));   
                    // im Falle Ablieferungstyp FILE macht die Validierung nichts
                    valid = true;
                    
                } else{
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_AE_ABLIEFERUNGSTYPUNDEFINED));                                
                    return false;
                }
                
            } catch (Exception e){
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        e.getMessage());                                
                return false;
            }
            
            zipfile.close();
            is.close();
            
        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Bd) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                                
            return false;
        } 
        
        return valid;
    }

}
