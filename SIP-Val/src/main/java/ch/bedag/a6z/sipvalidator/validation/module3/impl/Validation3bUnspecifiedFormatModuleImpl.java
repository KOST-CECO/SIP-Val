package ch.bedag.a6z.sipvalidator.validation.module3.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.gov.nationalarchives.droid.core.signature.droid4.Droid;
import uk.gov.nationalarchives.droid.core.signature.droid4.FileFormatHit;
import uk.gov.nationalarchives.droid.core.signature.droid4.IdentificationFile;
import uk.gov.nationalarchives.droid.core.signature.droid4.signaturefile.FileFormat;
import ch.bedag.a6z.sipvalidator.exception.module3.Validation3bUnspecifiedFormatException;
import ch.bedag.a6z.sipvalidator.service.ConfigurationService;
import ch.bedag.a6z.sipvalidator.util.Util;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module3.Validation3bUnspecifiedFormatModule;

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

        String nameOfSignature = getConfigurationService().getNameOfDroidSignatureFile();
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
