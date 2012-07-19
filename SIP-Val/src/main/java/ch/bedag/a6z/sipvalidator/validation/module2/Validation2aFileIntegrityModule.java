package ch.bedag.a6z.sipvalidator.validation.module2;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module2.Validation2aFileIntegrityException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 2b
 * 
 * Dateien mit Prüfsumme in (metadata.xml) //pruefsumme validieren.
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation2aFileIntegrityModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation2aFileIntegrityException;

}
