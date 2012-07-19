package ch.bedag.a6z.sipvalidator.validation.module1;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module1.Validation1eSipTypeException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 1e
 * 
 * Der SIP Typ wird ermittelt und angezeigt (metadata.xml)//ablieferungstyp: GEVER oder FILE
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation1eSipTypeModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation1eSipTypeException;

}
