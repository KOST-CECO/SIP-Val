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

package ch.kostceco.bento.sipval.validation.module3.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.gov.nationalarchives.droid.core.signature.droid4.Droid;
import uk.gov.nationalarchives.droid.core.signature.droid4.FileFormatHit;
import uk.gov.nationalarchives.droid.core.signature.droid4.IdentificationFile;
import uk.gov.nationalarchives.droid.core.signature.droid4.signaturefile.FileFormat;
import ch.kostceco.bento.sipval.exception.module3.Validation3aFormatRecognitionException;
import ch.kostceco.bento.sipval.service.ConfigurationService;
import ch.kostceco.bento.sipval.util.Util;
import ch.kostceco.bento.sipval.validation.ValidationModuleImpl;
import ch.kostceco.bento.sipval.validation.module3.Validation3aFormatRecognitionModule;

/**
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 */

public class Validation3aFormatRecognitionModuleImpl extends
		ValidationModuleImpl implements Validation3aFormatRecognitionModule
{

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
	public boolean validate( File sipDatei, File directoryOfLogfile )
			throws Validation3aFormatRecognitionException
	{
		boolean valid = true;

		Map<String, File> filesInSipFile = new HashMap<String, File>();

		String nameOfSignature = getConfigurationService()
				.getPathToDroidSignatureFile();
		Integer zaehlerWait = 1;
		System.out.print( getTextResourceService().getText(
				MESSAGE_MODULE_WAITZAEHLER, zaehlerWait ) );
		System.out.flush();

		if ( nameOfSignature == null ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_Ca )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ getTextResourceService().getText(
									MESSAGE_CONFIGURATION_ERROR_NO_SIGNATURE ) );

			System.out
					.print( "\r                                                                                                                                     " );
			System.out.flush();
			System.out.print( "\r" );
			System.out.flush();

			return false;
		}

		Droid droid = null;
		try {
			// kleiner Hack, weil die Droid libraries irgendwo ein System.out
			// drin haben, welche
			// den Output st�ren

			// Util.switchOffConsole() als Kommentar markieren wenn man die
			// Fehlermeldung
			// erhalten m�chte @Rc
			Util.switchOffConsole();
			droid = new Droid();

			String pathOfDroidConfig = getConfigurationService()
					.getPathOfDroidSignatureFile();
			droid.readSignatureFile( pathOfDroidConfig );

		} catch ( Exception e ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_Ca )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ getTextResourceService().getText(
									ERROR_CANNOT_INITIALIZE_DROID ) );

			System.out
					.print( "\r                                                                                                                                     " );
			System.out.flush();
			System.out.print( "\r" );
			System.out.flush();

			return false;
		} finally {
			Util.switchOnConsole();
		}

		// Die Archivdatei wurde bereits vom Schritt 1d in das
		// Arbeitsverzeichnis entpackt
		String pathToWorkDir = getConfigurationService().getPathToWorkDir();
		File workDir = new File( pathToWorkDir );
		Map<String, File> fileMap = Util.getFileMap( workDir, true );
		Set<String> fileMapKeys = fileMap.keySet();
		for ( Iterator<String> iterator = fileMapKeys.iterator(); iterator
				.hasNext(); ) {
			String entryName = iterator.next();
			File newFile = fileMap.get( entryName );

			if ( !newFile.isDirectory() ) {
				filesInSipFile.put( entryName, newFile );
			}
		}

		Map<String, String> hPuids = getConfigurationService()
				.getAllowedPuids();
		Map<String, Integer> counterPuid = new HashMap<String, Integer>();

		Set<String> fileKeys = filesInSipFile.keySet();

		for ( Iterator<String> iterator = fileKeys.iterator(); iterator
				.hasNext(); ) {
			String fileKey = iterator.next();
			File file = filesInSipFile.get( fileKey );

			IdentificationFile ifile = droid.identify( file.getAbsolutePath() );

			if ( ifile.getNumHits() > 0 ) {

				for ( int x = 0; x < ifile.getNumHits(); x++ ) {
					FileFormatHit ffh = ifile.getHit( x );
					FileFormat ff = ffh.getFileFormat();

					String extensionConfig = hPuids.get( ff.getPUID() );

					if ( extensionConfig == null ) {
						valid = false;

						if ( counterPuid.get( ff.getPUID() ) == null ) {
							counterPuid.put( ff.getPUID(), new Integer( 1 ) );
						} else {
							Integer count = counterPuid.get( ff.getPUID() );
							counterPuid.put( ff.getPUID(),
									new Integer( count.intValue() + 1 ) );
						}
					}

				}

			}

			System.out
					.print( "\r                                                                                                                                     " );
			System.out.flush();
			System.out.print( "\r" );
			System.out.flush();

			zaehlerWait = zaehlerWait + 1;

			System.out.print( getTextResourceService().getText(
					MESSAGE_MODULE_WAITZAEHLER, zaehlerWait ) );
			System.out.flush();

		}

		Set<String> keysExt = counterPuid.keySet();
		for ( Iterator<String> iterator = keysExt.iterator(); iterator
				.hasNext(); ) {
			String keyExt = iterator.next();
			Integer value = counterPuid.get( keyExt );
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_Ca )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ keyExt
							+ " = "
							+ value.toString()
							+ getTextResourceService().getText(
									MESSAGE_MODULE_CA_FILES ) );

			System.out
					.print( "\r                                                                                                                                     " );
			System.out.flush();
			System.out.print( "\r" );
			System.out.flush();

			valid = false;
		}

		System.out
				.print( "\r                                                                                                                                     " );
		System.out.flush();
		System.out.print( "\r" );
		System.out.flush();

		return valid;
	}

}
