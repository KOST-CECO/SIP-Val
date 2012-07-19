package ch.bedag.a6z.sipvalidator.validation.module1.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1eSipTypeException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module1.Validation1eSipTypeModule;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

/**
 * 
 * Der SIP Typ wird angezeigt: GEVER oder FILE (ermittelt aus dem metadata.xml, element ablieferung)
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class Validation1eSipTypeModuleImpl extends ValidationModuleImpl implements Validation1eSipTypeModule {


    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(File sipDatei) throws Validation1eSipTypeException {

        FileEntry metadataxml = null;

        String toplevelDir = sipDatei.getName();
        int lastDotIdx = toplevelDir.lastIndexOf(".");
        toplevelDir = toplevelDir.substring(0, lastDotIdx);

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
                        getTextResourceService().getText(MESSAGE_MODULE_Ae) + 
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
                                        
                XPath xpath = XPathFactory.newInstance().newXPath();
                Element elementName = (Element)xpath.evaluate("/paket/ablieferung", doc, XPathConstants.NODE);
            
                if (elementName == null) {
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Ae) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_AE_ABLIEFERUNGSTYPUNDEFINED));  
                    return false;
                }
                
                if (elementName.getAttribute("xsi:type").equals("ablieferungGeverSIP")) {
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Ae) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(MESSAGE_MODULE_AE_ABLIEFERUNGSTYPGEVER));                

                } else if (elementName.getAttribute("xsi:type").equals("ablieferungFilesSIP")) {
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Ae) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(MESSAGE_MODULE_AE_ABLIEFERUNGSTYPFILE));                
                } else{
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Ae) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_AE_ABLIEFERUNGSTYPUNDEFINED));  
                    return false;
                }
                
            } catch (Exception e){
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Ae) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        e.getMessage());                                
                return false;
            }
            
            zipfile.close();
            is.close();
            
        } catch (Exception e) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_Ae) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + e.toString());                                
            return false;

        } 
        
        return true;
    }

}
