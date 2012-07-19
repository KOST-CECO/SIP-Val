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

package ch.kostceco.bento.sipval.validation.module3.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.gov.nationalarchives.droid.core.signature.droid4.Droid;
import uk.gov.nationalarchives.droid.core.signature.droid4.FileFormatHit;
import uk.gov.nationalarchives.droid.core.signature.droid4.IdentificationFile;
import uk.gov.nationalarchives.droid.core.signature.droid4.signaturefile.FileFormat;
import ch.kostceco.bento.sipval.exception.module3.Validation3bUnspecifiedFormatException;
import ch.kostceco.bento.sipval.service.ConfigurationService;
import ch.kostceco.bento.sipval.util.Util;
import ch.kostceco.bento.sipval.validation.ValidationModuleImpl;
import ch.kostceco.bento.sipval.validation.module3.Validation3bUnspecifiedFormatModule;

public class Validation3bUnspecifiedFormatModuleImpl extends ValidationModuleImpl implements Validation3bUnspecifiedFormatModule {
    
    private ConfigurationService configurationService;
    
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public boolean validate(File sipDatei) throws Validation3bUnspecifiedFormatException {
        boolean valid = true;
        
        Map<String, File> filesInSipFile = new HashMap<String, File>();

        /**
         * 
         * getNameOfDroidSignatureFile wird durch getPathToDroidSignatureFile ersetzt und wurde entsprechend als Kommentar markiert.
         * Maskierte Lösung funktioniert nur wenn es die Bedag kompiliert.
         * 
         * @author Rc Claire Röthlisberger-Jourdan, KOST-CECO, @version 0.2.1, date 28.03.2011
         *
         * String nameOfSignature = getConfigurationService().getNameOfDroidSignatureFile();  
         *       
         */
        String nameOfSignature = getConfigurationService().getPathToDroidSignatureFile();
        if (nameOfSignature == null) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cb) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(MESSAGE_CONFIGURATION_ERROR_NO_SIGNATURE));                                
            return false;
        }

        Droid droid = null;
        try {
            // kleiner Hack, weil die Droid libraries irgendwo ein System.out drin haben, welche
            // den Output stören
            Util.switchOffConsole();
            droid = new Droid();
            
            String pathOfDroidConfig = getConfigurationService().getPathOfDroidSignatureFile();            
            droid.readSignatureFile(pathOfDroidConfig);

        }
        catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cb) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(ERROR_CANNOT_INITIALIZE_DROID));                                
            return false;
        } finally {
            Util.switchOnConsole();            
        }
        
        // TODO: es wäre viel besser, wenn die DROID Identifikation auch über Streams statt über Files
        // durchgeführt werden könnte. Noch keine Ahnung, ob und wie das möglich ist. Die Dokumentation
        // zu Droid ist quasi nicht vorhanden.
       
        // Die Archivdatei wurde bereits vom Schritt 1d in das Arbeitsverzeichnis entpackt
        String pathToWorkDir = getConfigurationService().getPathToWorkDir();
        File workDir = new File(pathToWorkDir);
        Map<String, File> fileMap = Util.getFileMap(workDir, false);
        Set<String> fileMapKeys = fileMap.keySet();
        for (Iterator<String> iterator = fileMapKeys.iterator(); iterator.hasNext();) {
            String entryName = iterator.next();
            File newFile = fileMap.get(entryName);
            
            if (!newFile.isDirectory() && entryName.contains("content/")) {
                filesInSipFile.put(entryName, newFile);
            }
        }

        
        Map<String, String> hPuids = getConfigurationService().getAllowedPuids();
        Map<String, Integer> counterPuid = new HashMap<String, Integer>();
        
        Set<String> fileKeys = filesInSipFile.keySet();
        
        for (Iterator<String> iterator = fileKeys.iterator(); iterator.hasNext();) {
            String fileKey = iterator.next();

            File file = filesInSipFile.get(fileKey);

            IdentificationFile ifile = droid.identify(file.getAbsolutePath());
            
            if (ifile.getNumHits() > 0) {
                
                for (int x = 0; x < ifile.getNumHits(); x++) {
                    FileFormatHit ffh = ifile.getHit(x);
                    FileFormat ff = ffh.getFileFormat();
                    
                    String extensionConfig = hPuids.get(ff.getPUID());
                    
                    if (extensionConfig == null) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Cb) + 
                                getTextResourceService().getText(MESSAGE_DASHES) +  fileKey + " (" + ff.getPUID() + ")");
                        valid = false;
                        
                        if (counterPuid.get(ff.getPUID()) == null) {
                            counterPuid.put(ff.getPUID(), new Integer(1));
                        } else {
                            Integer count = counterPuid.get(ff.getPUID());
                            counterPuid.put(ff.getPUID(), new Integer(count.intValue() + 1));
                        }
                    } 
                    
                }

            }
            
        }
        /*
        Set<String> keysExt = counterPuid.keySet();
        for (Iterator<String> iterator = keysExt.iterator(); iterator.hasNext();) {
            String keyExt = iterator.next();
            Integer value = counterPuid.get(keyExt);
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cb) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + keyExt + " = " + value.toString() + 
                    getTextResourceService().getText(MESSAGE_MODULE_CA_FILES));
            valid = false;
        }
        */
        return valid;
    }

}
