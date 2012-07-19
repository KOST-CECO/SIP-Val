/*== SIP-Val ==================================================================================
The SIP-Val application is used for validate Submission Information Package (SIP).
Copyright (C) 2011 Claire Röthlisberger (KOST-CECO), Daniel Ludin (BEDAG AG)
$Id: ValidatedFormat.java 14 2011-07-21 07:07:28Z u2044 $
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

package ch.kostceco.bento.sipval.service.vo;
/**
 * Ein Value Object, das die "validatedformat" Elemente aus der Konfigurationsdatei einkapselt.
 * @author razm Daniel Ludin, Bedag AG @version 0.2.0
 */
public class ValidatedFormat {
    
    final static String JHOVE = "JHOVE";
    final static String PDFTRON = "PDFTRON";
    
    private String pronomUniqueId;
    private String validator;
    private String extension;
    private String description;
    
    public ValidatedFormat(String pronomUniqueId, String validator, String extension, String description) {
        super();
        this.pronomUniqueId = pronomUniqueId;
        this.validator = validator;
        this.extension = extension;
        this.description = description;
    }
    
    public String getPronomUniqueId() {
        return pronomUniqueId;
    }
    public void setPronomUniqueId(String pronomUniqueId) {
        this.pronomUniqueId = pronomUniqueId;
    }
    public String getValidator() {
        return validator;
    }
    public void setValidator(String validator) {
        this.validator = validator;
    }
    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
