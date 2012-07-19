/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.signature;

import java.awt.Cursor;
import java.awt.Frame;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.gui.DialogUtils;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;
import uk.gov.nationalarchives.droid.signature.SignatureManagerException;

/**
 * @author rflitcroft
 *
 */
public class CheckSignatureUpdateAction extends SwingWorker<SignatureFileInfo, Void> {

    private final Log log = LogFactory.getLog(getClass());

    private SignatureManager signatureManager;
    private SignatureFileInfo signatureFileInfo;
    private SignatureUpdateProgressDialog progressDialog;
    private Frame parent;

    private boolean error;

    /**
     * 
     * Default constructor.
     */
    public CheckSignatureUpdateAction() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected SignatureFileInfo doInBackground() throws SignatureManagerException {
        SignatureFileInfo sigFileInfo = signatureManager.getLatestSignatureFile();
        return sigFileInfo;
    }
    
    /**
     * Starts the task.
     * @param parentFrame the parent of this task
     */
    public void start(Frame parentFrame) {
        this.parent = parentFrame;

        progressDialog = new SignatureUpdateProgressDialog(parent);

        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        execute();
        progressDialog.setVisible(true);
        if (progressDialog.isCancelled()) {
            cancel(true);
        }
    }
    
    /**
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
        try {
            signatureFileInfo = get();
        } catch (ExecutionException e) {
            error = true;
            progressDialog.setVisible(false);
            log.error(e.getCause());
            DialogUtils.showSignatureUpdateErrorDialog(parent, e.getCause());
        } catch (InterruptedException e) {
            log.warn(e.getCause());
        } catch (CancellationException e) {
            log.warn(e);
        } finally {
            progressDialog.setVisible(false);
            progressDialog.dispose();
            parent.setCursor(Cursor.getDefaultCursor());
        }
    }
    
    /**
     * @param signatureManager the signatureManager to set
     */
    public void setSignatureManager(SignatureManager signatureManager) {
        this.signatureManager = signatureManager;
    }
    
    /**
     * @param progressDialog the progressDialog to set
     */
    public void setProgressDialog(SignatureUpdateProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
    
    /**
     * @return the signatureFileInfo
     */
    public SignatureFileInfo getSignatureFileInfo() {
        return signatureFileInfo;
    }

    /**
     * @return true if the check failed, false otherwise.
     */
    public boolean hasError() {
        return error;
    }
    
}
