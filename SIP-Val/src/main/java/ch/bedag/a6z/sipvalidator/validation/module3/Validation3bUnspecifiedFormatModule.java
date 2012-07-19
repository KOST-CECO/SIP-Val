package ch.bedag.a6z.sipvalidator.validation.module3;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module3.Validation3bUnspecifiedFormatException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 3b
 * 
 * Alle nicht spezifizierten Dateien mit entsprechenden Formatangaben auflisten. 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation3bUnspecifiedFormatModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation3bUnspecifiedFormatException;
}
