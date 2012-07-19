package ch.bedag.a6z.sipvalidator.service;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.SystemException;


/**
 * 
 * Service Interface für Jhove.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public interface JhoveService extends Service {
       
    /**
     * Führt die Validierung mit Jhove aus
     * 
     * @param pathToJhoveJar    Pfad zur JHove Applikation (JhoveApp.jar)
     * @param pathToInputFile   Pfad(e) zu den Input-Files, mit Blanks separiert
     * @param pathToOutput      Pfad zum Output-File
     * @param nameOfSip         Name des Original-SIP-Input Files
     * @param extension         die Extension der Input Files
     * @return                  Name des Output Files (der pathToOutput wurde eventuell umbenannt)
     * @throws SystemException
     */
    public File executeJhove(
        String pathToJhoveJar, String pathToInputFile, String pathToOutput, String nameOfSip, String extension) 
        throws SystemException;
}
