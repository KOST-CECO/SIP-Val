package ch.bedag.a6z.sipvalidator.validation.module2;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module2.Validation2bChecksumException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 2a
 * 
 * Überprüfen, ob alle in (metadata.xml) /paket/inhaltsverzeichnis referenzierten 
 * Dateien vorhanden sind. Fehlende Dateien auflisten.
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation2bChecksumModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation2bChecksumException;

}
