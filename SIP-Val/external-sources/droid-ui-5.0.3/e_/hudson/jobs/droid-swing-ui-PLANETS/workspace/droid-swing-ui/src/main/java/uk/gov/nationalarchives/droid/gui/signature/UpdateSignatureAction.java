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
import java.util.SortedMap;
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
public class UpdateSignatureAction extends SwingWorker<SignatureFileInfo, Void> {

    private final Log log = LogFactory.getLog(getClass());
    
    
    private SignatureManager signatureManager;
    private SignatureFileInfo signatureFileInfo;
    
    private Frame parent;

    private SignatureUpdateProgressDialog progressDialog;
    
    /**
     * Default constructor.
     */
    public UpdateSignatureAction() {
    }
    
    /**
     * Starts the action.
     * @param parentFrame the parent frame
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
     * {@inheritDoc}
     */
    @Override
    protected SignatureFileInfo doInBackground() throws SignatureManagerException {
        return signatureManager.downloadLatest();
    }

    /**
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
        try {
            SignatureFileInfo update = get();
            progressDialog.setVisible(false);
            DialogUtils.showUpdateSuccessfulDialog(parent, update);
        } catch (InterruptedException e) {
            log.warn(e.getCause());
        } catch (ExecutionException e) {
            log.error(e.getCause());
            progressDialog.setVisible(false);
            DialogUtils.showSignatureUpdateErrorDialog(parent, e.getCause());
        } catch (CancellationException e) {
            log.warn(e);
        } finally {
            progressDialog.setVisible(false);
            progressDialog.dispose();
            parent.setCursor(Cursor.getDefaultCursor());
        }
    }
    
    /**
     * @return the signatureFileInfo
     */
    public SignatureFileInfo getSignatureFileInfo() {
        return signatureFileInfo;
    }
    
    /**
     * @param signatureManager the signatureManager to set
     */
    public void setSignatureManager(SignatureManager signatureManager) {
        this.signatureManager = signatureManager;
    }

    /**
     * @return all available signature files
     */
    public SortedMap<String, SignatureFileInfo> getAllSignatureFiles() {
        return signatureManager.getAvailableSignatureFiles();
    }
    
    
}
