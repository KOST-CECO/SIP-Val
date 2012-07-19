package ch.bedag.a6z.sipvalidator.validation.module1;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1bFolderStructureException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 1b
 * 
 * Besteht eine korrekte primäre Verzeichnisstruktur: 
 * /header/metadata.xml, /header/xsd und /content? 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation1bFolderStructureModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation1bFolderStructureException;
   

}
