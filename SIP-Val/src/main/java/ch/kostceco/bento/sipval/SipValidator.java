/*== SIP-Val ==================================================================================
The SIP-Val v4.1.2 application is used for validate Submission Information Package (SIP).
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

package ch.kostceco.bento.sipval;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.kostceco.bento.sipval.controller.Controller;
import ch.kostceco.bento.sipval.logging.LogConfigurator;
import ch.kostceco.bento.sipval.logging.Logger;
import ch.kostceco.bento.sipval.logging.MessageConstants;
import ch.kostceco.bento.sipval.service.ConfigurationService;
import ch.kostceco.bento.sipval.service.TextResourceService;
import ch.kostceco.bento.sipval.util.Util;
import ch.kostceco.bento.sipval.util.Zip64Archiver;

/**
 * Dies ist die Starter-Klasse, verantwortlich f�r das Initialisieren des
 * Controllers, des Loggings und das Parsen der Start-Parameter.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 */
public class SipValidator implements MessageConstants
{

	private static final Logger		LOGGER	= new Logger( SipValidator.class );

	private TextResourceService		textResourceService;
	private ConfigurationService	configurationService;

	public TextResourceService getTextResourceService()
	{
		return textResourceService;
	}

	public void setTextResourceService( TextResourceService textResourceService )
	{
		this.textResourceService = textResourceService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(
			ConfigurationService configurationService )
	{
		this.configurationService = configurationService;
	}

	/**
	 * Die Minimaleingabe besteht aus Parameter 1: Pfad zum SIP-File Parameter
	 * 2: Pfad zum Logging-Verzeichnis
	 * 
	 * Optional: Parameter 3: die optionalen Validierungsschritte (+3c oder +3d)
	 * Parameter 4: die optionalen Validierungsschritte (+3d)
	 * 
	 * Beispiel: java -jar C:\ludin\A6Z-SIP-Validator\SIP-Beispiele
	 * etc\1.1.1.a)_SIP_20101018_RIS_4.zip C:\ludin\sipvalidator-logs +3d
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:config/applicationContext.xml" );

		// TODO: siehe Bemerkung im applicationContext-services.xml bez�glich
		// Injection in der Superklasse aller Impl-Klassen
		// ValidationModuleImpl validationModuleImpl = (ValidationModuleImpl)
		// context.getBean("validationmoduleimpl");

		SipValidator sipValidator = (SipValidator) context
				.getBean( "sipvalidator" );

		// Ist die Anzahl Parameter (2) korrekt?
		if ( args.length < 2 ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_PARAMETER_USAGE ) );
			System.exit( 1 );
		}

		File sipDatei = new File( args[0] );

		LOGGER.logInfo( sipValidator.getTextResourceService().getText(
				MESSAGE_SIPVALIDATION, sipDatei.getName() ) );

		// die Anwendung muss mindestens unter Java 6 laufen
		String javaRuntimeVersion = System.getProperty( "java.vm.version" );
		if ( javaRuntimeVersion.compareTo( "1.6.0" ) < 0 ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_WRONG_JRE ) );
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Ueberpr�fung des 2. Parameters (Log-Verzeichnis)
		File directoryOfLogfile = new File( args[1] );
		if ( !directoryOfLogfile.exists() ) {
			directoryOfLogfile.mkdir();
		}

		// Im Logverzeichnis besteht kein Schreibrecht
		if ( !directoryOfLogfile.canWrite() ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NOTWRITABLE, directoryOfLogfile ) );
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		if ( !directoryOfLogfile.isDirectory() ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NODIRECTORY ) );
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Informationen zum Arbeitsverzeichnis holen
		String pathToWorkDir = sipValidator.getConfigurationService()
				.getPathToWorkDir();
		/*
		 * Nicht vergessen in
		 * "src/main/resources/config/applicationContext-services.xml" beim
		 * entsprechenden Modul die property anzugeben: <property
		 * name="configurationService" ref="configurationService" />
		 */

		File tmpDir = new File( pathToWorkDir );
		// bestehendes Workverzeichnis ggf. l�schen und wieder anlegen
		if ( tmpDir.exists() ) {
			Util.deleteDir( tmpDir );
		}
		if ( !tmpDir.exists() ) {
			tmpDir.mkdir();
		}

		// Im workverzeichnis besteht kein Schreibrecht
		if ( !tmpDir.canWrite() ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_WORKDIRECTORY_NOTWRITABLE, tmpDir ) );
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Ueberpr�fung des 1. Parameters (SIP-Datei): existiert die Datei?
		if ( !sipDatei.exists() ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_SIPFILE_FILENOTEXISTING ) );
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Ueberpr�fung des 1. Parameters (SIP-Datei): ist die Datei ein
		// Verzeichnis?
		// Wenn ja, wird im work-Verzeichnis eine Zip-Datei daraus erstellt,
		// damit die weiteren
		// Validierungen Gebrauch von der java.util.zip API machen k�nnen, und
		// somit die zu Validierenden
		// Archive gleichartig behandelt werden k�nnen, egal ob es sich um eine
		// Verzeichnisstruktur oder ein
		// Zip-File handelt.
		// Informationen zum Arbeitsverzeichnis holen

		String originalSipName = sipDatei.getAbsolutePath();
		if ( sipDatei.isDirectory() ) {
			if ( tmpDir.exists() ) {
				Util.deleteDir( tmpDir );
			}
			tmpDir.mkdir();

			try {
				File targetFile = new File( pathToWorkDir, sipDatei.getName()
						+ ".zip" );
				Zip64Archiver.archivate( sipDatei, targetFile );
				sipDatei = targetFile;

			} catch ( Exception e ) {
				LOGGER.logInfo( sipValidator.getTextResourceService().getText(
						ERROR_CANNOTCREATEZIP ) );
				System.exit( 1 );
			}

		} else {
			// L�schen des Arbeitsverzeichnisses, falls eines angelegt wurde
			File workDir = new File( pathToWorkDir );
			if ( workDir.exists() ) {
				Util.deleteDir( workDir );
			}
			workDir.mkdir();
		}

		// Ueberpr�fung der optionalen Parameter (3. und 4.)
		if ( args.length == 3
				&& !(args[2].equals( "+3c" ) || args[2].equals( "+3d" )) ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_PARAMETER_OPTIONAL_1 ) );
			System.exit( 1 );
		}

		if ( args.length == 4
				&& !(args[2].equals( "+3c" ) && args[3].equals( "+3d" )) ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					ERROR_PARAMETER_OPTIONAL_2 ) );
			System.exit( 1 );
		}

		if ( args.length > 2 && args[2].equals( "+3c" ) ) {
			// �berpr�fen der Konfiguration: existiert die JHoveApp.jar am
			// angebenen Ort?
			String jhoveApp = sipValidator.getConfigurationService()
					.getPathToJhoveJar();
			File fJhoveApp = new File( jhoveApp );
			if ( !fJhoveApp.exists()
					|| !fJhoveApp.getName().equals( "JhoveApp.jar" ) ) {
				LOGGER.logInfo( sipValidator.getTextResourceService().getText(
						ERROR_JHOVEAPP_MISSING ) );
				System.exit( 1 );
			}

			// �berpr�fen der Konfiguration: existiert die jhove.conf am
			// angebenen Ort?
			String jhoveConf = sipValidator.getConfigurationService()
					.getPathToJhoveConfiguration();
			File fJhoveConf = new File( jhoveConf );
			if ( !fJhoveConf.exists()
					|| !fJhoveConf.getName().equals( "jhove.conf" ) ) {
				LOGGER.logInfo( sipValidator.getTextResourceService().getText(
						ERROR_JHOVECONF_MISSING ) );
				System.exit( 1 );
			}
		}

		// Konfiguration des Loggings, ein File Logger wird zus�tzlich erstellt
		LogConfigurator logConfigurator = (LogConfigurator) context
				.getBean( "logconfigurator" );
		String logFileName = logConfigurator.configure(
				directoryOfLogfile.getAbsolutePath(), sipDatei.getName() );

		LOGGER.logError( sipValidator.getTextResourceService().getText(
				MESSAGE_SIPVALIDATION, sipDatei.getName() ) );

		Controller controller = (Controller) context.getBean( "controller" );
		boolean okMandatory = controller.executeMandatory( sipDatei, directoryOfLogfile );
		boolean ok = false;

		// die Validierungen 1a - 1d sind obligatorisch, wenn sie bestanden
		// wurden, k�nnen die restlichen
		// Validierungen, welche nicht zum Abbruch der Applikation f�hren,
		// ausgef�hrt werden.
		if ( okMandatory ) {

			ok = controller.executeOptional( sipDatei, directoryOfLogfile );

			// Ausf�hren der beiden optionalen Schritte
			if ( args.length > 2 && args[2].equals( "+3c" ) ) {
				boolean ok3c = controller.execute3c( sipDatei, directoryOfLogfile );
				ok = ok && ok3c;
			}

			if ( args.length > 2 && args[2].equals( "+3d" ) ) {
				boolean ok3d = controller.execute3d( sipDatei, directoryOfLogfile );
				ok = ok && ok3d;
			}

			if ( args.length > 3 && args[3].equals( "+3d" ) ) {
				boolean ok3d = controller.execute3d( sipDatei, directoryOfLogfile );
				ok = ok && ok3d;
			}

		}

		ok = (ok && okMandatory);

		LOGGER.logInfo( "" );
		if ( ok ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_TOTAL_VALID, sipDatei.getAbsolutePath() ) );
		} else {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_TOTAL_INVALID, sipDatei.getAbsolutePath() ) );
		}
		LOGGER.logInfo( "" );

		// Ausgabe der Pfade zu den Jhove/Pdftron & Co. Reports, falls welche
		// generiert wurden
		if ( Util.getPathToReportJHove() != null ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_FOOTER_REPORTJHOVE, Util.getPathToReportJHove() ) );
		}
		if ( Util.getPathToReportPdftron() != null ) {
			LOGGER.logInfo( sipValidator.getTextResourceService()
					.getText( MESSAGE_FOOTER_REPORTPDFTRON,
							Util.getPathToReportPdftron() ) );
		}
		if ( Util.getPathToReportSiardVal() != null ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_FOOTER_REPORTSIARDVAL,
					Util.getPathToReportSiardVal() ) );
		}

		LOGGER.logInfo( "" );
		LOGGER.logInfo( sipValidator.getTextResourceService().getText(
				MESSAGE_FOOTER_SIP, originalSipName ) );
		LOGGER.logInfo( sipValidator.getTextResourceService().getText(
				MESSAGE_FOOTER_LOG, logFileName ) );
		LOGGER.logInfo( "" );

		if ( okMandatory ) {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_VALIDATION_FINISHED ) );
		} else {
			LOGGER.logInfo( sipValidator.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
		}

		// L�schen des Arbeitsverzeichnisses, falls eines angelegt wurde

		File workDir = new File( pathToWorkDir );
		if ( workDir.exists() ) {
			Util.deleteDir( workDir );
		}
		if ( ok ) {
			System.exit( 0 );
			// L�schen des Arbeitsverzeichnisses, falls eines angelegt wurde
			if ( workDir.exists() ) {
				Util.deleteDir( workDir );
			}

		} else {
			System.exit( 2 );
			// L�schen des Arbeitsverzeichnisses, falls eines angelegt wurde
			if ( workDir.exists() ) {
				Util.deleteDir( workDir );
			}
			if ( workDir.exists() ) {
				Util.deleteDir( workDir );
			}
		}

	}

}
