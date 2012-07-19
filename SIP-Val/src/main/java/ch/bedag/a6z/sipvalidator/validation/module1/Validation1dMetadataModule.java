package ch.bedag.a6z.sipvalidator.validation.module1;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1dMetadataException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 1d
 * 
 * Entspricht metadata.xml den Schemadateien in /header/xsd? 
 * Allfällige XML Fehler werden angezeigt.
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation1dMetadataModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation1dMetadataException;

}
