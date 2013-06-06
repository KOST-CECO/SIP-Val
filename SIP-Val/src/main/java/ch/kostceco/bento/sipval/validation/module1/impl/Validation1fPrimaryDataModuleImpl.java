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

package ch.kostceco.bento.sipval.validation.module1.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.kostceco.bento.sipval.exception.module1.Validation1fPrimaryDataException;
import ch.kostceco.bento.sipval.validation.ValidationModuleImpl;
import ch.kostceco.bento.sipval.validation.module1.Validation1fPrimaryDataModule;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

/**
 * Diese Validierung gibt true (OK) zur�ck, wenn keine Prim�rdateien im
 * Verzeichnis content vorhanden sind, der Ablieferungstyp aber GEVER ist. Sind
 * keine Prim�rdateien im Verzeichnis content vorhanden, der Ablieferungstyp ist
 * jedoch FILE, ist dies ein Fehler und gibt false zur�ck.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 */
public class Validation1fPrimaryDataModuleImpl extends ValidationModuleImpl
		implements Validation1fPrimaryDataModule
{

	private static final String	NAMEOFCONTENTFOLDER	= "content/";

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate( File sipDatei, File directoryOfLogfile )
			throws Validation1fPrimaryDataException
	{

		String toplevelDir = sipDatei.getName();
		int lastDotIdx = toplevelDir.lastIndexOf( "." );
		toplevelDir = toplevelDir.substring( 0, lastDotIdx );

		FileEntry metadataxml = null;
		boolean contentFolderEmpty = true;

		try {
			Zip64File zipfile = new Zip64File( sipDatei );
			List<FileEntry> fileEntryList = zipfile.getListFileEntries();
			for ( FileEntry fileEntry : fileEntryList ) {

				if ( fileEntry.getName().equals( "header/" + METADATA )
						|| fileEntry.getName().equals(
								toplevelDir + "/header/" + METADATA ) ) {
					metadataxml = fileEntry;
					break;
				}
				if ( fileEntry.getName().startsWith( NAMEOFCONTENTFOLDER )
						&& fileEntry.getName().length() > NAMEOFCONTENTFOLDER
								.length() ) {
					contentFolderEmpty = false;
				} else if ( fileEntry.getName().startsWith(
						toplevelDir + "/" + NAMEOFCONTENTFOLDER )
						&& fileEntry.getName().length() > (toplevelDir + "/" + NAMEOFCONTENTFOLDER)
								.length() ) {

					contentFolderEmpty = false;
				}

			}

			// keine metadata.xml in der SIP-Datei gefunden
			if ( metadataxml == null ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_Af )
								+ getTextResourceService().getText(
										MESSAGE_DASHES )
								+ getTextResourceService().getText(
										ERROR_MODULE_AE_NOMETADATAFOUND ) );
				return false;

			}

			EntryInputStream eis = zipfile.openEntryInputStream( metadataxml
					.getName() );
			BufferedInputStream is = new BufferedInputStream( eis );

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse( is );

				XPath xpath = XPathFactory.newInstance().newXPath();
				Element elementName = (Element) xpath.evaluate(
						"/paket/ablieferung", doc, XPathConstants.NODE );

				zipfile.close();
				is.close();

				if ( elementName == null ) {
					getMessageService()
							.logError(
									getTextResourceService().getText(
											MESSAGE_MODULE_Af )
											+ getTextResourceService().getText(
													MESSAGE_DASHES )
											+ getTextResourceService()
													.getText(
															ERROR_MODULE_AE_ABLIEFERUNGSTYPUNDEFINED ) );
					return false;
				}

				if ( !contentFolderEmpty ) {
					return true;
				} else {
					if ( elementName.getAttribute( "xsi:type" ).equals(
							"ablieferungGeverSIP" ) ) {
						getMessageService()
								.logError(
										getTextResourceService().getText(
												MESSAGE_MODULE_Af )
												+ getTextResourceService()
														.getText(
																MESSAGE_DASHES )
												+ getTextResourceService()
														.getText(
																MESSAGE_MODULE_AF_GEVERSIPWITHOUTPRIMARYDATA ) );
						return true;
					} else if ( elementName.getAttribute( "xsi:type" ).equals(
							"ablieferungFilesSIP" ) ) {
						getMessageService()
								.logError(
										getTextResourceService().getText(
												MESSAGE_MODULE_Af )
												+ getTextResourceService()
														.getText(
																MESSAGE_DASHES )
												+ getTextResourceService()
														.getText(
																ERROR_MODULE_AF_FILESIPWITHOUTPRIMARYDATA ) );
						return false;
					} else {
						getMessageService()
								.logError(
										getTextResourceService().getText(
												MESSAGE_MODULE_Af )
												+ getTextResourceService()
														.getText(
																MESSAGE_DASHES )
												+ getTextResourceService()
														.getText(
																ERROR_MODULE_AE_ABLIEFERUNGSTYPUNDEFINED ) );
						return false;
					}
				}

			} catch ( Exception e ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_MODULE_Af )
								+ getTextResourceService().getText(
										MESSAGE_DASHES ) + e.getMessage() );
				return false;

			}

		} catch ( Exception e ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_MODULE_Af )
							+ getTextResourceService().getText( MESSAGE_DASHES )
							+ e.getMessage() );
			return false;
		}

	}

}
