/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.config;

import java.util.SortedMap;

import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;

/**
 * @author rflitcroft
 *
 */
public class ListSignatureFilesAction {

    private SignatureManager signatureManager;
    
    /**
     * 
     * @return a map of available signature files.
     */
    public SortedMap<String, SignatureFileInfo> list() {
        return signatureManager.getAvailableSignatureFiles();
    }
    
    /**
     * @param signatureManager the signatureManager to set
     */
    public void setSignatureManager(SignatureManager signatureManager) {
        this.signatureManager = signatureManager;
    }
}
