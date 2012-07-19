package ch.bedag.a6z.sipvalidator.validation.module1;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1cNamingException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 1c
 * 
 * Entsprechen die Verzeichnis- und Dateinamen den Einschränkungen in der Spezifikation? 
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation1cNamingModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation1cNamingException;

}
