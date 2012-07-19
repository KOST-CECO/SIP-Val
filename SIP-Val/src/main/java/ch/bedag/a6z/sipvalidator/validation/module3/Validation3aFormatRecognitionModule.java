package ch.bedag.a6z.sipvalidator.validation.module3;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module3.Validation3aFormatRecognitionException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 3a
 * 
 * Mit DROID (und der file format registry PRONOM) oder ähnlichen Lösungen eine Formaterkennung 
 * durchführen und die Formate mit der Liste erlaubter Dateiformate vergleichen (Liste konfigurierbar). 
 * Die verwendete Lösung wird im Systemdesign bestimmt. 
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation3aFormatRecognitionModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation3aFormatRecognitionException;
    
}
