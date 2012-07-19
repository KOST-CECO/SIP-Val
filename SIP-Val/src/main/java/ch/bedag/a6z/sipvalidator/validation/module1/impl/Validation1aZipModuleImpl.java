package ch.bedag.a6z.sipvalidator.validation.module1.impl;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1aZipException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module1.Validation1aZipModule;
import ch.enterag.utils.zip.Zip64File;
/**
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class Validation1aZipModuleImpl extends ValidationModuleImpl implements Validation1aZipModule {

    @Override
    public boolean validate(File sipDatei) throws Validation1aZipException {

        
        // wenn die Datei kein Directory ist, muss sie mit zip oder zip64 enden
        // TODO: was ist mir .rar Dateien?
        if (! (sipDatei.getAbsolutePath().toLowerCase().endsWith(".zip") || 
               sipDatei.getAbsolutePath().toLowerCase().endsWith(".zip64") )) {
            
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_Aa) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(ERROR_MODULE_A_INCORRECTFILEENDING));                
            
            return false;
        }
        
        Zip64File zf = null;
        
        try { 
            // Versuche das ZIP file zu öffnen
            zf = new Zip64File(sipDatei);
            // und wenn es klappt, gleich wieder schliessen
            zf.close();
            
        } catch (Exception e) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_Aa) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + e.getMessage());                
            
            return false;
        }
        
        return true;
    }
    

}
