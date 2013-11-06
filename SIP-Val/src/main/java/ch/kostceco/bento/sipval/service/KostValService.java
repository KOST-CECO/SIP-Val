/*== SIP-Val ==================================================================================
The SIP-Val application is used for validate Submission Information Package (SIP).
Copyright (C) 2011-2013 Claire Röthlisberger (KOST-CECO), Daniel Ludin (BEDAG AG)
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

import ch.kostceco.bento.sipval.exception.SystemException;

/**
 * Service Interface für KOST-Val.
 * 
 * @author Rc Claire Röthlisberger-Jourdan, KOST-CECO
 */
public interface KostValService extends Service
{

	/**
	 * Gibt den Pfad zum KOST-Val Executable zurück
	 * 
	 * @return Pfad zum KOST-Val Executable
	 */
	public String getPathToKostValJar();

	/**
	 * Setzt den Pfad zum KOST-Val Executable
	 * 
	 * @return Pfad zum KOST-Val Executable
	 */
	public void setPathToKostValJar( String pathToKostValJar );

	/**
	 * Gibt den Pfad zum Input File (das zu validierende Dokument) zurück
	 * 
	 * @return Pfad zum Input File
	 */
	public String getPathToInputFile();

	/**
	 * Setzt den Pfad zum Input File (dem zu validierenden Dokument)
	 * 
	 * @return Pfad zum Input File
	 */
	public void setPathToInputFile( String pathToInputFile );

	/**
	 * Führt die Validierung mit KOST-Val aus
	 * 
	 * @return Pfad zum von KOST-Val generierten LOG-Report
	 * @throws Exception
	 */
	public String executeKostVal( String pathToKostValJar,
			String pathToInputFile, String pathToOutput, String nameOfSip )
			throws SystemException;
}
