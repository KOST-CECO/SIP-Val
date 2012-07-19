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

package ch.kostceco.bento.sipval.validation.module1.impl;

import java.io.File;

import ch.kostceco.bento.sipval.exception.module1.Validation1aZipException;
import ch.kostceco.bento.sipval.validation.ValidationModuleImpl;
import ch.kostceco.bento.sipval.validation.module1.Validation1aZipModule;
import ch.enterag.utils.zip.Zip64File;
/**
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 *
 */
public class Validation1aZipModuleImpl extends ValidationModuleImpl implements Validation1aZipModule {

    @Override
    public boolean validate(File sipDatei) throws Validation1aZipException {

        
        // wenn die Datei kein Directory ist, muss sie mit zip oder zip64 enden
        // TODO: was ist mir .rar Dateien?
        if (! (sipDatei.getAbsolutePath().toLowerCase().endsWith(".zip") || 
               sipDatei.getAbsolutePath().toLowerCase().endsWith(".zip64") )) {
            
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_Aa) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(ERROR_MODULE_A_INCORRECTFILEENDING));                
            
            return false;
        }
        
        Zip64File zf = null;
        
        try { 
            // Versuche das ZIP file zu öffnen
            zf = new Zip64File(sipDatei);
            // und wenn es klappt, gleich wieder schliessen
            zf.close();
            
        } catch (Exception e) {
            getMessageService().logError(getTextResourceService().getText(MESSAGE_MODULE_Aa) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + e.getMessage());                
            
            return false;
        }
        
        return true;
    }
    

}
