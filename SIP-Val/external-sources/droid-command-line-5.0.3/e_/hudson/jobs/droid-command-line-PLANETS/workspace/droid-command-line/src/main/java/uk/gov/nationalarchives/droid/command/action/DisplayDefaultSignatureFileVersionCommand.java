/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command.action;

import java.io.PrintWriter;

import uk.gov.nationalarchives.droid.command.i18n.I18N;
import uk.gov.nationalarchives.droid.signature.SignatureFileException;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;

/**
 * @author rflitcroft
 *
 */
public class DisplayDefaultSignatureFileVersionCommand implements DroidCommand {
    
    private PrintWriter printWriter;
    private SignatureManager signatureManager;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws CommandExecutionException {
        SignatureFileInfo sigFileInfo;
        try {
            sigFileInfo = signatureManager.getDefaultSignature();
            printWriter.println(I18N.getResource(I18N.DEFAULT_SIGNATURE_VERSION,
                    sigFileInfo.getVersion(), sigFileInfo.getFile().getName()));
        } catch (SignatureFileException e) {
            throw new CommandExecutionException(e);
        }
    }
    
    /**
     * @param printWriter the printWriter to set
     */
    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
    
    /**
     * @param signatureManager the signatureManager to set
     */
    public void setSignatureManager(SignatureManager signatureManager) {
        this.signatureManager = signatureManager;
    }

}
