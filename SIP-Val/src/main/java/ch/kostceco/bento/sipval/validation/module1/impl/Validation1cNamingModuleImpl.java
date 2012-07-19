/*== SIP-Val ==================================================================================
The SIP-Val application is used for validate Submission Information Package (SIP).
Copyright (C) 2011 Claire Röthlisberger (KOST-CECO), Daniel Ludin (BEDAG AG)
$Id: Validation1cNamingModuleImpl.java 14 2011-07-21 07:07:28Z u2044 $
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

package ch.kostceco.bento.sipval.validation.module1.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.kostceco.bento.sipval.exception.module1.Validation1cNamingException;
import ch.kostceco.bento.sipval.service.ConfigurationService;
import ch.kostceco.bento.sipval.validation.ValidationModuleImpl;
import ch.kostceco.bento.sipval.validation.module1.Validation1cNamingModule;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;
/**
 * Diverse Validierungen zu den Namen der Files und Ordner, erlaubte Längen, verwendete Zeichen usw.
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 */
public class Validation1cNamingModuleImpl extends ValidationModuleImpl implements Validation1cNamingModule {

    private ConfigurationService configurationService;
    
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(File sipDatei) throws Validation1cNamingException {

        boolean valid = true;
                
        String fileName = sipDatei.getName();
        
        // I.) Validierung der Namen aller Dateien: sind die enthaltenen Zeichen alle erlaubt?
                
        String patternStr = "[^!#\\$%\\(\\)\\+,\\-_\\.=@\\[\\]\\{\\}\\~a-zA-Z0-9 ]"; 
        Pattern pattern = Pattern.compile(patternStr); 
        
        try {
            Zip64File zipfile2 = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList2 = zipfile2.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList2) {
                
                String name = fileEntry.getName();
                
                String[] pathElements = name.split("/");
                for (int i = 0; i < pathElements.length; i++) {
                    String element = pathElements[i];

                    Matcher matcher = pattern.matcher(element); 
                    
                    boolean matchFound = matcher.find(); 
                    if (matchFound) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(MESSAGE_MODULE_AC_INVALIDCHARACTERS, element));                
                        return false;
                    }
                }
                
                zipfile2.close();
    
                
            }
        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                
        }
        
        
        // II.) Validierung des Formats des Dateinamen
        
        patternStr = getConfigurationService().getAllowedSipName();  
        Pattern p = Pattern.compile(patternStr); 
        Matcher matcher = p.matcher(fileName);
        
        boolean matchFound = matcher.find(); 
        if (!matchFound) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(MESSAGE_MODULE_AC_INVALIDFILENAME));                
            return false;
        }
        
        
        // III.) Validierung der beiden Second-Level-Ordner im Toplevel-Ordner, es müssen genau header/ und content/ vorhanden sein
        // und nichts anderes.
        
        List<String> listOfFolders = new ArrayList<String>();
        
        try {
            
            String toplevelDir = sipDatei.getName();
            int lastDotIdx = toplevelDir.lastIndexOf(".");
            toplevelDir = toplevelDir.substring(0, lastDotIdx);

            
            Zip64File zipfile = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
                
                String name = fileEntry.getName();

                if ((((name.equals("header/") || name.equals(toplevelDir + "/" + "header/")) && fileEntry.isDirectory())) ||
                        (((name.equals("content/") || name.equals(toplevelDir + "/" + "content/")) && fileEntry.isDirectory())) ) {
                    
                    listOfFolders.add(name);
                }
                
                if ((name.startsWith("header/") || name.startsWith(toplevelDir + "/" + "header/")) && !name.endsWith(".xsd")) {
                    if (!(name.endsWith("header/metadata.xml") || name.endsWith("header/xsd/") || name.endsWith("header/"))) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(MESSAGE_MODULE_AC_NOTALLOWEDFILE, name));
                        
                        return false;
                        
                    }
                        
                }
                
                // alle file namen müssen mit content oder header anfangen
                if (!(name.startsWith(toplevelDir) || 
                        name.startsWith("content/") || 
                        name.startsWith("header/") || 
                        name.startsWith(toplevelDir + "/" + "content/") || 
                        name.startsWith(toplevelDir + "/" + "header/"))) {
                    
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(MESSAGE_MODULE_AC_NOTALLOWEDFILE, name));
                    
                    return false;
                }
                
            }
            zipfile.close();

            if (listOfFolders.size() != 2) {
                return false;
            }
            
        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                
            
            return false;

        }
        
        
        // IV.+V.) Validierung des header Ordners: es dürfen sich nur ein weiterer folder namens xsd und eine file namens
        // metadata.xml darin befinden. Im xsd Folder wiederum dürfen sich nur eine Reihe *.xsd files befinden wie definiert
        // im Konfigurationsfile.
        
        List<String> allowedXsd = getConfigurationService().getAllowedXsdFileNames();
        // generiert eine Map mit den xsd-files und Ordnern, welche in header/xsd/ enthalten sein müssen
        Map<String, String> allowedXsdFiles = new HashMap<String, String>();
        allowedXsdFiles.put("header/", "header/");
        allowedXsdFiles.put("header/xsd/", "header/xsd/");
        allowedXsdFiles.put("header/metadata.xml", "header/metadata.xml/");
        for (Iterator<String> iterator = allowedXsd.iterator(); iterator.hasNext();) {
            String xsdfilename = iterator.next();
            allowedXsdFiles.put("header/xsd/" + xsdfilename, "header/xsd/" + xsdfilename);    
        }
        
        try {
            Zip64File zipfile = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
                
                String name = fileEntry.getName();
                
                // wenn das SIP-Archiv einen Top-Level-Folder enthält, der gleich heisst wie das SIP-Archiv,
                // müssen die Pfade entsprechend korrigiert werden
                String toplevelDir = sipDatei.getName();
                int lastDotIdx = toplevelDir.lastIndexOf(".");
                toplevelDir = toplevelDir.substring(0, lastDotIdx);
                                
                if (name.startsWith(toplevelDir)) {
                    String folderToSubtract = toplevelDir + "/";
                    name = name.replace(folderToSubtract, "");
                }
                
                if (name.startsWith("header/")) {
                    String removedEntry = allowedXsdFiles.remove(name);
                    if (removedEntry == null) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(MESSAGE_MODULE_AC_NOTALLOWEDFILE, name));
                        valid = false;
                    }
                }
                
            }
            zipfile.close();
            
            Set<String> keys = allowedXsdFiles.keySet();
            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
                String string = iterator.next();
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        getTextResourceService().getText(MESSAGE_MODULE_AC_MISSINGFILE, string));
                valid = false;
            }
            
        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                
            
            return false;
        }
        
        
        // VI.+ VII) Länge der Pfade (< 180) und File/Ordnernamen (<41)
        
        Integer maxPathLength = getConfigurationService().getMaximumPathLength();
        Integer maxFileLength = getConfigurationService().getMaximumFileLength();
        try {
            Zip64File zipfile = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
                
                String name = sipDatei.getName() + "/" + fileEntry.getName();

                if (name.length() > maxPathLength.intValue()) {
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(MESSAGE_MODULE_AC_PATHTOOLONG, name));
                    valid = false;
                    break;
                }
                
                String[] pathElements = name.split("/");
                for (int i = 0; i < pathElements.length; i++) {
                    String pathElement = pathElements[i];
                    if (pathElement.length() > maxFileLength.intValue()) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(MESSAGE_MODULE_AC_FILENAMETOOLONG, pathElement));
                        valid = false;
                    }
                }
                
            }
            zipfile.close();
            
        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Ac) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                
            
            return false;

        }
        
        
        return valid;
    }

    public static void main(String[] args) {
        
        File sipDatei = new File("C:\\ludin\\A6Z-SIP-Validator\\SIP-Beispiele etc\\SIP_20101018_RIS_4.zip");
        
        Validation1cNamingModuleImpl module = new Validation1cNamingModuleImpl();
        try {
            module.validate(sipDatei);
        } catch (Validation1cNamingException e) {
            e.printStackTrace();
        }
    }
 
}
