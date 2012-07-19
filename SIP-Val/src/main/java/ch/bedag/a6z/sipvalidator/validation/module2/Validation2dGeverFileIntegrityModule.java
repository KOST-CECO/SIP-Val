package ch.bedag.a6z.sipvalidator.validation.module2;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module2.Validation2dGeverFileIntegrityException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 2d
 * 
 * Bei GEVER SIP prüfen, ob alle in (metadata.xml) /paket/inhaltsverzeichnis/content 
 * referenzierten Dateien auch in (metadata.xml)/paket/ablieferung/ordnungsystem verzeichnet sind. 
 * Allfällige Inkonsistenzen auflisten. ( //dokument[@id] => //datei[@id] ).
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation2dGeverFileIntegrityModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation2dGeverFileIntegrityException;

}
