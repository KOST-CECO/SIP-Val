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
import java.util.Map;

import uk.gov.nationalarchives.droid.command.i18n.I18N;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;

/**
 * @author rflitcroft
 *
 */
public class ListAllSignatureFilesCommand implements DroidCommand {

    private SignatureManager signatureManager;
    private PrintWriter printWriter;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Map<String, SignatureFileInfo> sigFiles = signatureManager.getAvailableSignatureFiles();
        if (sigFiles.isEmpty()) {
            printWriter.println(I18N.getResource(I18N.NO_SIG_FILES_AVAILABLE));
        } else {
            for (SignatureFileInfo info : sigFiles.values()) {
                printWriter.println(I18N.getResource(I18N.DEFAULT_SIGNATURE_VERSION,
                        info.getVersion(), info.getFile().getName()));
            }
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
