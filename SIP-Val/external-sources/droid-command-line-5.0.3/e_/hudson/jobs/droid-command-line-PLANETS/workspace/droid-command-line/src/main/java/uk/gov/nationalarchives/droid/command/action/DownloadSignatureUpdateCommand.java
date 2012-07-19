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
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;
import uk.gov.nationalarchives.droid.signature.SignatureManagerException;

/**
 * @author rflitcroft
 *
 */
public class DownloadSignatureUpdateCommand implements DroidCommand {

    private SignatureManager signatureManager;
    private PrintWriter printWriter;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws CommandExecutionException {
        SignatureFileInfo sigfFileInfo;
        try {
            sigfFileInfo = signatureManager.downloadLatest();
            printWriter.println(I18N.getResource(I18N.DOWNLOAD_SIGNATURE_UPDATE_SUCCESS, sigfFileInfo.getVersion()));
        } catch (SignatureManagerException e) {
            throw new CommandExecutionException(
                I18N.getResource(I18N.DOWNLOAD_SIGNATURE_UPDATE_ERROR, 
                        e.getCause().getMessage(), e.getCauseType(), e.getCauseMessage()), 
                e.getCause());
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
