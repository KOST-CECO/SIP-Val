package ch.bedag.a6z.sipvalidator.validation.module3;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module3.Validation3cFormatValidationException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 3c
 * 
 * (einschaltbar) 
 * 
 * Formatvalidierung, mit JHOVE oder einer ähnlichen Lösung sowie 
 * mit einem externem PDF/A-Validator (z.B. PDF/A Manager von PDFTRON) aller nach Dateiformat 
 * ausgewählten Dateien in /content (konfigurierbare Liste von den zu validierenden Dateiformaten). 
 * Referenzierung in Log-Datei zu den allfälligen zusätzlich generierten Logdateien der eingesetzten Programme. 
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation3cFormatValidationModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation3cFormatValidationException;
    
}
