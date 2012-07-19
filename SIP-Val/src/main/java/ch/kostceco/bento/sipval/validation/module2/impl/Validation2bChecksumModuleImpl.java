/*== SIP-Val ==================================================================================
The SIP-Val v0.9.0 application is used for validate Submission Information Package (SIP).
Copyright (C) 2011 Claire Röthlisberger (KOST-CECO), Daniel Ludin (BEDAG AG)
-----------------------------------------------------------------------------------------------
SIP-Val is a development of the KOST-CECO. All rights rest with the KOST-CECO. 
This application is free software: you can redistribute it and/or modify it under the 
terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version. 
BEDAG AG and Daniel Ludin hereby disclaims all copyright interest in the program 
SIP-Val v0.2.0 written by Daniel Ludin (BEDAG AG). Switzerland, 1 March 2011.
This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the follow GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; 
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA 02110-1301 USA or see <http://www.gnu.org/licenses/>.
==============================================================================================*/

package ch.kostceco.bento.sipval.validation.module2.impl;

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

import ch.kostceco.bento.sipval.exception.module2.Validation2bChecksumException;
import ch.kostceco.bento.sipval.validation.ValidationModuleImpl;
import ch.kostceco.bento.sipval.validation.module2.Validation2bChecksumModule;
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
                    /**
                     * 
                     * String ERROR_MODULE_BB_MISSINGINSIP als Kommentarmarkiert, da diese bereits beim
                     * 2a ausgegeben wird und nicht eine Fehlermeldung von 2b ist
                     * 
                     * @author Rc Claire Röthlisberger-Jourdan, KOST-CECO, @version 0.2.1, date 06.04.2011
                     *
                     * getMessageService().logError(
                     *       getTextResourceService().getText(MESSAGE_MODULE_Bb) + 
                     *       getTextResourceService().getText(MESSAGE_DASHES) + 
                     *       getTextResourceService().getText(ERROR_MODULE_BB_MISSINGINSIP, keyMetadata));
                     *       
                     */         
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
