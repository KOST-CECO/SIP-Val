/*== SIP-Val ==================================================================================
The SIP-Val v0.9.0 application is used for validate Submission Information Package (SIP).
Copyright (C) 2011 Claire Röthlisberger (KOST-CECO), Daniel Ludin (BEDAG AG)
-----------------------------------------------------------------------------------------------
SIP-Val is a development of the KOST-CECO. All rights rest with the KOST-CECO. 
This application is free software: you can redistribute it and/or modify it under the 
terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version. 
BEDAG AG and Daniel Ludin hereby disclaims all copyright interest in the program 
SIP-Val v0.2.0 written by Daniel Ludin (BEDAG AG). Switzerland, 1 March 2011.
This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the follow GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; 
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA 02110-1301 USA or see <http://www.gnu.org/licenses/>.
==============================================================================================*/

package ch.kostceco.bento.sipval.service;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import ch.kostceco.bento.sipval.service.vo.ValidatedFormat;

/**
 * 
 * Service Interface für die Konfigurationsdatei.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
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
     *
     * getNameOfDroidSignatureFile wird durch getPathToDroidSignatureFile ersetzt und wurde entsprechend als Kommentar markiert.
     * Maskierte Lösung funktioniert nur wenn es die Bedag kompiliert.
     * @author Rc Claire Röthlisberger-Jourdan, KOST-CECO, @version 0.2.1, date 28.03.2011
     * 
     * String getNameOfDroidSignatureFile();  
     *  
     */

    /**
     * 
     * Gibt den Namen und den Padf des DROID Signature Files zurück. Die Signaturen werden laufend aktualisiert
     * und müssen deshalb von Zeit zu Zeit ausgetauscht werden. Daher der konfigurierbare Name.
     * 
     * @return Namen und Pfad des DROID Signature Files
     */
    String getPathToDroidSignatureFile();
    
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
