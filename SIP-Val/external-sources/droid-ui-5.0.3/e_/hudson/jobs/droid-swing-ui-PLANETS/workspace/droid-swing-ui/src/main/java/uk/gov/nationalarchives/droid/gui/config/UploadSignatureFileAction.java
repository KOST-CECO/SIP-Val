/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.config;

import java.awt.Window;
import java.io.File;

import javax.swing.JOptionPane;

import uk.gov.nationalarchives.droid.signature.SignatureFileException;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;

/**
 * @author rflitcroft
 *
 */
public class UploadSignatureFileAction {

    private SignatureManager signatureManager;
    
    private String fileName;
    private boolean useAsDefault;
    
    /**
     * Executes this action.
     * @param parent the parent window
     */
    public void execute(Window parent) {
        File f = new File(fileName);
        try {
            SignatureFileInfo info = signatureManager.upload(f, useAsDefault);
            String message = String.format("Signature file %s has been uploaded", info.getFile().getName());
            JOptionPane.showMessageDialog(parent, message, "Signature file uploaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (SignatureFileException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), 
                    "Error uploading signature file", JOptionPane.ERROR_MESSAGE);
        }            
    }

    /**
     * @param signatureManager the signatureManager to set
     */
    public void setSignatureManager(SignatureManager signatureManager) {
        this.signatureManager = signatureManager;
    }
    
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * @param useAsDefault the useAsDefault to set
     */
    public void setUseAsDefault(boolean useAsDefault) {
        this.useAsDefault = useAsDefault;
    }
}
