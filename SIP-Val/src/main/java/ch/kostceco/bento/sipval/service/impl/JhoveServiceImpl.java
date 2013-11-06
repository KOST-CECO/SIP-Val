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

package ch.kostceco.bento.sipval.service.impl;

import java.io.File;
//import java.util.Date;

import ch.kostceco.bento.sipval.exception.SystemException;
import ch.kostceco.bento.sipval.logging.Logger;
import ch.kostceco.bento.sipval.service.ConfigurationService;
import ch.kostceco.bento.sipval.service.JhoveService;
import ch.kostceco.bento.sipval.util.StreamGobbler;
import ch.kostceco.bento.sipval.util.Util;

/**
 * Dieser Service stellt die Schnittstelle zur JHove Software dar.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 */
public class JhoveServiceImpl implements JhoveService
{

	private static final Logger		LOGGER	= new Logger(
													JhoveServiceImpl.class );

	private ConfigurationService	configurationService;

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(
			ConfigurationService configurationService )
	{
		this.configurationService = configurationService;
	}

	@Override
	public File executeJhove( String pathToJhoveJar, String pathToInputFile,
			String pathToOutput, String nameOfSip, String extension )
			throws SystemException
	{

		File newReport = new File( pathToOutput, nameOfSip + ".jhove-log.txt" );

		File jhoveJar = new File( pathToJhoveJar ); // Pfad zum Programm JHove
		StringBuffer command = new StringBuffer( "java -jar " + jhoveJar + " " );

		String jhoveConfig = getConfigurationService()
				.getPathToJhoveConfiguration();

		// das passende Modul zur jeweiligen File-Extension ausw�hlen

		if ( extension.equals( "gif" ) ) {
			command.append( "-m gif-hul " );
		} else if ( extension.equals( "html" ) || extension.equals( "htm" ) ) {
			command.append( "-m html-hul " );
		} else if ( extension.equals( "jpg" ) || extension.equals( "jpeg" )
				|| extension.equals( "jpe" ) || extension.equals( "jfif" )
				|| extension.equals( "jfi" ) || extension.equals( "jif" )
				|| extension.equals( "jls" ) || extension.equals( "spf" ) ) {
			command.append( "-m jpeg-hul " );
		} else if ( extension.equals( "jp2" ) || extension.equals( "jpg2" )
				|| extension.equals( "j2c" ) || extension.equals( "jpf" )
				|| extension.equals( "jpx" ) ) {
			command.append( "-m jpeg2000-hul " );
		} else if ( extension.equals( "pdf" ) ) {
			command.append( "-m pdf-hul " );
		} else if ( extension.equals( "wav" ) || extension.equals( "wave" )
				|| extension.equals( "bwf" ) ) {
			command.append( "-m wave-hul " );
		} else if ( extension.equals( "xml" ) || extension.equals( "xsd" ) ) {
			command.append( "-m xml-hul " );
		} else {
			// wird in "Validation3cFormatValidationModuleImpl.java" abgefangen
		}

		command.append( " -c " );
		command.append( "\"" );
		command.append( jhoveConfig );
		command.append( "\" " );

		command.append( "-o " );
		command.append( "\"" );
		command.append( newReport.getAbsolutePath() );
		command.append( "\"" );
		command.append( " " );
		command.append( pathToInputFile );

		try {

			Runtime rt = Runtime.getRuntime();

			Process proc = rt.exec( command.toString() );

			// Fehleroutput holen
			StreamGobbler errorGobbler = new StreamGobbler(
					proc.getErrorStream(), "ERROR" );

			// Output holen
			StreamGobbler outputGobbler = new StreamGobbler(
					proc.getInputStream(), "OUTPUT" );

			Util.switchOffConsole();

			// Threads starten
			errorGobbler.start();
			outputGobbler.start();

			// Warte, bis wget fertig ist
			proc.waitFor();

			Util.switchOnConsole();

		} catch ( Exception e ) {
			LOGGER.logDebug( "JHove Service failed: " + e.getMessage() );
			throw new SystemException( e.toString() );
		}

		return newReport;

	}

}
