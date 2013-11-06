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

package ch.kostceco.bento.sipval.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.kostceco.bento.sipval.exception.SystemException;
import ch.kostceco.bento.sipval.logging.Logger;
import ch.kostceco.bento.sipval.service.KostValService;
import ch.kostceco.bento.sipval.service.TextResourceService;
import ch.kostceco.bento.sipval.util.StreamGobbler;
import ch.kostceco.bento.sipval.util.Util;

/**
 * Dieser Service stellt die Schnittstelle zur SIARD-Val Software dar.
 * 
 * @author Rc Claire Röthlisberger-Jourdan, KOST-CECO
 */
public class KostValServiceImpl implements KostValService
{

	private static final Logger	LOGGER	= new Logger( KostValServiceImpl.class );

	private TextResourceService	textResourceService;

	public TextResourceService getTextResourceService()
	{
		return textResourceService;
	}

	public void setTextResourceService( TextResourceService textResourceService )
	{
		this.textResourceService = textResourceService;
	}

	@Override
	public String executeKostVal( String pathToKostValJar,
			String pathToInputFile, String pathToOutput, String nameOfSip )
			throws SystemException
	{
		File report;
		// Pfad zum Programm KOST-Val
		File kostvalJar = new File( pathToKostValJar );
		// Pfad zur SIARD-Datei
		File input = new File( pathToInputFile );
		File output = new File( pathToOutput );
		StringBuffer command = new StringBuffer( "java -jar " + kostvalJar
				+ " " );

		command.append( pathToInputFile );
		command.append( " " );
		command.append( output.getAbsolutePath() );

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

			// Der Name des generierten Reports lautet per default
			// Dateinamen.Extension.validationlog.log
			// und es gibt keine Möglichkeit, dies zu übersteuern.
			String log = new String( input.getName() + ".validationlog.log" );

			report = new File( pathToOutput, log );
			File newReport = new File( pathToOutput, nameOfSip + ".kostval.log" );

			// falls das File bereits existiert, z.B. von einem vorhergehenden
			// Durchlauf, löschen wir es
			if ( newReport.exists() ) {
				newReport.delete();
			}

			// Rename funktioniert nicht zuverlässig. Entsprechend wird eine
			// Kopie angelegt
			InputStream inStream = null;
			OutputStream outStream = null;

			try {
				File afile = report;
				File bfile = newReport;
				inStream = new FileInputStream( afile );
				outStream = new FileOutputStream( bfile );
				byte[] buffer = new byte[1024];
				int length;
				// copy the file content in bytes
				while ( (length = inStream.read( buffer )) > 0 ) {
					outStream.write( buffer, 0, length );
				}
				inStream.close();
				outStream.close();

			} catch ( IOException e ) {
				e.printStackTrace();
			}
			report = newReport;

		} catch ( Exception e ) {
			LOGGER.logDebug( "KOST-Val Service failed: " + e.getMessage() );
			throw new SystemException( e.toString() );
		}
		return report.getAbsolutePath();
	}

	@Override
	public String getPathToInputFile()
	{
		return null;
	}

	@Override
	public String getPathToKostValJar()
	{
		return null;
	}

	@Override
	public void setPathToInputFile( String pathToInputFile )
	{

	}

	@Override
	public void setPathToKostValJar( String pathToKostValJar )
	{

	}

}
