package ch.bedag.a6z.sipvalidator.validation.module1.impl;

import java.io.File;
import java.util.List;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1bFolderStructureException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module1.Validation1bFolderStructureModule;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

/**
 * 
 * Besteht eine korrekte primäre Verzeichnisstruktur:
 * 
 * /header/metadata.xml /header/xsd /content
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 * 
 */
public class Validation1bFolderStructureModuleImpl extends ValidationModuleImpl implements
        Validation1bFolderStructureModule {

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(File sipDatei) throws Validation1bFolderStructureException {

        boolean bExistsXsdFolder = false;
        boolean bExistsContentFolder = false;
        boolean bExistsMetadataFile = false;
        
        String toplevelDir = sipDatei.getName();
        int lastDotIdx = toplevelDir.lastIndexOf(".");
        toplevelDir = toplevelDir.substring(0, lastDotIdx);
        
        try {
           
            Zip64File zipfile = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
                
                String name = fileEntry.getName();

                if ((name.equals("content/") || name.equals(toplevelDir + "/content/")) && (fileEntry.isDirectory())) {
                    bExistsContentFolder = true;
                }
                
                if ((name.equals("header/xsd/") || name.equals(toplevelDir + "/header/xsd/")) && (fileEntry.isDirectory())) {
                    bExistsXsdFolder = true;
                }
                
                if ((name.equals("header/metadata.xml") || name.equals(toplevelDir + "/header/metadata.xml")) && (!fileEntry.isDirectory())) {
                    bExistsMetadataFile = true;
                }
                
            }
            zipfile.close();
            
        } catch (Exception e) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_Ab) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + e.getMessage());                
            
            return false;

        }

        return (bExistsContentFolder && bExistsMetadataFile && bExistsXsdFolder);
    }

}
