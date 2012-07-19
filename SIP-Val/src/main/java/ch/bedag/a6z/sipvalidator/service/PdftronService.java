package ch.bedag.a6z.sipvalidator.service;

import ch.bedag.a6z.sipvalidator.exception.SystemException;


/**
 * 
 * Service Interface für Pdftron.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public interface PdftronService extends Service {
    
    /**
     * Gibt den Pfad zum Pdftron Executable zurück
     * @return Pfad zum Pdftron Executable
     */
    public String getPathToPdftronExe();
    
    /**
     * Setzt den Pfad zum Pdftron Executable
     * @return Pfad zum Pdftron Executable
     */
    public void setPathToPdftronExe(String pathToPdftronExe);
    
    /**
     * Gibt den Pfad zum Input File (das zu validierende Dokument) zurück 
     * @return Pfad zum Input File
     */
    public String getPathToInputFile();

    /**
     * Setzt den Pfad zum Input File (dem zu validierenden Dokument)
     * @return Pfad zum Input File
     */
    public void setPathToInputFile(String pathToInputFile);
    
    /**
     * Führt die Validierung mit Pdftron aus
     * @return Pfad zum von Pdftron generierten XML-Report
     * @throws Exception 
     */
    public String executePdftron(String pathToPdftronExe, String pathToInputFile, String pathToOutput, String nameOfSip) throws SystemException;
}
