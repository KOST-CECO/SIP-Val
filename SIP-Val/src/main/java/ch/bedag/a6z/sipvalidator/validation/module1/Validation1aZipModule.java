package ch.bedag.a6z.sipvalidator.validation.module1;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1aZipException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 1a
 * 
 * Ist das SIP eine korrekte, unkomprimierte ZIP / ZIP64 Datei 
 * (wenn komprimiert, mit welchem Algorithmus)? 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0 Daniel Ludin, Bedag AG
 * @version 2.0
 *
 */

public interface Validation1aZipModule extends ValidationModule {

    public boolean validate(File sipDatei) throws Validation1aZipException;
    
}
