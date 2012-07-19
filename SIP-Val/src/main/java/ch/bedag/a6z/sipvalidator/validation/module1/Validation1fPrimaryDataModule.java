package ch.bedag.a6z.sipvalidator.validation.module1;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1fPrimaryDataException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 1f
 * 
 * Es wird angezeigt, falls keine Primärdateien im Verzeichnis /content vorhanden sind 
 * (korrektes GEVER SIP nur zur Archivierung einer Ordnerstruktur aber fehlerhaftes FILE SIP).
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation1fPrimaryDataModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation1fPrimaryDataException;

}
