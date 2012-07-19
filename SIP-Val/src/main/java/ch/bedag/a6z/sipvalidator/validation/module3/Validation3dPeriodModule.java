package ch.bedag.a6z.sipvalidator.validation.module3;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.module3.Validation3dPeriodException;
import ch.bedag.a6z.sipvalidator.validation.ValidationModule;

/**
 * Validierungsschritt 3d
 * 
 * (einschaltbar) 
 * 
 * Zeitraum-Validierung. Ältestes und Jüngstes Datum in einer Ordnungssystem Einheit (Dossier, Rubrik) 
 * müssen ohne Überlappung nach oben aggregierbar sein, Lehrräume sind aber erlaubt. 
 * Dies bedeutet, dass die Dokumente im Zeitraum des Dossiers sein müssen, 
 * diese wiederum in der Rubrik und entsprechend auch im SIP. 
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface Validation3dPeriodModule extends ValidationModule {

    
    public boolean validate(File sipDatei) throws Validation3dPeriodException;

}
