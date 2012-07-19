package ch.bedag.a6z.sipvalidator.service;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import ch.bedag.a6z.sipvalidator.service.vo.ValidatedFormat;

/**
 * 
 * Service Interface für die Konfigurationsdatei.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public interface ConfigurationService extends Service {
    
    /**
     * Gibt eine Liste mit den Namen der XSD-files aus, welche im header/xsd/ Verzeichnis liegen müssen.
     * 
     * @return Liste mit den Namen der XSD-files aus, welche im header/xsd/ Verzeichnis liegen müssen.
     */
    List<String> getAllowedXsdFileNames();
    
    /**
     * Gibt eine Liste mit den PUIDs aus, welche im SIP vorkommen dürfen.
     * 
     * @return Liste mit den PUIDs aus, welche im SIP vorkommen dürfen.
     */
    Map<String, String> getAllowedPuids();
    
    /**
     * Gibt die Maximal erlaubte Länge eines Pfades in der SIP-Datei aus.
     * 
     * @return Maximal erlaubte Länge eines Pfades in der SIP-Datei
     */
    Integer getMaximumPathLength();
    
    /**
     * Gibt die Maximal erlaubte Länge eines Files oder Orners in der SIP-Datei aus.
     * 
     * 
     * @return Maximal erlaubte Länge eines Files oder Orners in der SIP-Datei
     */
    Integer getMaximumFileLength();
    
    /**
     * 
     * Gibt den Namen des DROID Signature Files zurück. Die Signaturen werden laufend aktualisiert
     * und müssen deshalb von Zeit zu Zeit ausgetauscht werden. Daher der konfigurierbare Name.
     * 
     * @return Namen des DROID Signature Files
     */
    String getNameOfDroidSignatureFile();
    
    String getPathOfDroidSignatureFile() throws MalformedURLException;
    
    /**
     * Gibt den Pfad zum Pdftron Exe zurück.
     * @return Pfad zum Pdftron Exe
     */
    String getPathToPdftronExe();
    
    /**
     * Gibt den Pfad zum Output Folder des Pdftron zurück.
     * @return Pfad zum Output Folder des Pdftron
     */
    String getPathToPdftronOutputFolder();
    
    
    String getPathToJhoveJar();
    
    String getPathToJhoveOutput();
    
    String getPathToJhoveConfiguration();
    
    /**
     * Gibt den Pfad des Arbeitsverzeichnisses zurück.
     * Dieses Verzeichnis wird z.B. zum Entpacken des .zip-Files verwendet.
     * 
     * @return Pfad des Arbeitsverzeichnisses
     */
    String getPathToWorkDir();
    
    /**
     * Gibt eine Liste mit den zu validierenden Formaten zurück.
     * @return Liste mit den zu validierenden Formaten
     */
    List<ValidatedFormat> getValidatedFormats();
}
