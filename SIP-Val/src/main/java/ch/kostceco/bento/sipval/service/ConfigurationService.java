/*== SIP-Val ==================================================================================
The SIP-Val application is used for validate Submission Information Package (SIP).
Copyright (C) 2011-2013 Claire R�thlisberger (KOST-CECO), Daniel Ludin (BEDAG AG)
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

//import org.apache.tools.ant.util.regexp.Regexp;

import ch.kostceco.bento.sipval.service.vo.ValidatedFormat;

/**
 * Service Interface f�r die Konfigurationsdatei.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 */
public interface ConfigurationService extends Service
{

	/**
	 * Gibt eine Liste mit den PUIDs aus, welche im SIP vorkommen d�rfen.
	 * 
	 * @return Liste mit den PUIDs aus, welche im SIP vorkommen d�rfen.
	 */
	Map<String, String> getAllowedPuids();

	/**
	 * Gibt die Maximal erlaubte L�nge eines Pfades in der SIP-Datei aus.
	 * 
	 * @return Maximal erlaubte L�nge eines Pfades in der SIP-Datei
	 */
	Integer getMaximumPathLength();

	/**
	 * Gibt die Maximal erlaubte L�nge eines Files oder Orners in der SIP-Datei
	 * aus.
	 * 
	 * @return Maximal erlaubte L�nge eines Files oder Orners in der SIP-Datei
	 */
	Integer getMaximumFileLength();

	/**
	 * Neu soll die Einschr�nkung des SIP-Namen konfigurierbar sein ->
	 * getAllowedSipName.
	 * 
	 * @author Rc Claire R�thlisberger-Jourdan, KOST-CECO, @version 0.9.1, date
	 *         16.05.2011
	 */
	String getAllowedSipName();

	/**
	 * Gibt den Namen des DROID Signature Files zur�ck. Die Signaturen werden
	 * laufend aktualisiert und m�ssen deshalb von Zeit zu Zeit ausgetauscht
	 * werden. Daher der konfigurierbare Name.
	 * 
	 * @return Namen des DROID Signature Files
	 * @author Rc Claire R�thlisberger-Jourdan, KOST-CECO, @version 0.2.1, date
	 *         28.03.2011
	 */
	String getPathToDroidSignatureFile();

	String getPathOfDroidSignatureFile() throws MalformedURLException;

	/**
	 * Gibt den Pfad zum Pdftron Exe zur�ck.
	 * 
	 * @return Pfad zum Pdftron Exe
	 */
	String getPathToPdftronExe();

	/**
	 * Gibt den Pfad zum Siard-Val Exe zur�ck.
	 * 
	 * @return Pfad zum Siard-Val Exe
	 */
	String getPathToSiardValJar();


	String getPathToJhoveJar();


	String getPathToJhoveConfiguration();

	/**
	 * Gibt den Pfad des Arbeitsverzeichnisses zur�ck. Dieses Verzeichnis wird
	 * z.B. zum Entpacken des .zip-Files verwendet.
	 * 
	 * @return Pfad des Arbeitsverzeichnisses
	 */
	String getPathToWorkDir();

	/**
	 * Gibt eine Liste mit den zu validierenden Formaten zur�ck.
	 * 
	 * @return Liste mit den zu validierenden Formaten
	 */
	List<ValidatedFormat> getValidatedFormats();
	
	/**
	 * Gibt aus ob die BAR Version 1 und 4= eCH Version 1 erlaubt ist oder nicht.
	 * 
	 * @return 1 wenn erlaubt respektive 0 wenn nicht erlaubt
	 */
	Integer getAllowedVersionBar1();
	Integer getAllowedVersionBar4Ech1();
}
