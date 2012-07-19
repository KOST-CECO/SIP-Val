package ch.bedag.a6z.sipvalidator.validation.module2;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module2.Validation2cSurplusFilesException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 2c
 * 
 * Im SIP vorhandene, aber nicht in (metadata.xml)/paket/inhaltsverzeichnis 
 * verzeichnete Dateien auflisten (ausser Datei metadata.xml).
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation2cSurplusFilesModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation2cSurplusFilesException;

}
