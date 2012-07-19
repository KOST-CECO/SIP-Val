/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui;

import java.awt.Frame;

import javax.swing.JOptionPane;

import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;

/**
 * Utility class for diaplyaing dialogs.
 * @author rflitcroft
 *
 */
public final class DialogUtils {

    /** */
    private static final String SIGNATURE_UPDATE = "Signature update";

    private DialogUtils() { }
    
    /**
     * Shows the 'update available' YES_NO dialog.
     * @param parent the parent of the dialog
     * @param newVersion the version number to display
     * @return the dialog response
     */
    static int showUpdateAvailableDialog(Frame parent, int newVersion) {
        return JOptionPane.showConfirmDialog(
                parent,
                String.format("Signature update v.%s is available. Do you want to download it?", newVersion),
                SIGNATURE_UPDATE,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

    }
    
    /**
     * Shows the 'update was successful' dialog.
     * @param parent the dialog's parent
     * @param signatureFileInfo the downlaoded signature file
     */
    public static void showUpdateSuccessfulDialog(Frame parent, SignatureFileInfo signatureFileInfo) {
        JOptionPane.showMessageDialog(parent,
                String.format("Signature file version %s downloaded successfully.",
                        signatureFileInfo.getVersion()),
                SIGNATURE_UPDATE,
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows the 'update was successful' dialog.
     * @param parent the dialog's parent
     */
    static void showNothingIsSelectedForRemoveDialog(Frame parent) {
        JOptionPane.showMessageDialog(parent,
                "Nothing is selected to remove.",
                "Nothing selected to remove.",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    /**
     * Shows the 'update not available' dialog.
     * @param parent the dialog's parent
     */
    static void showUpdateUnavailableDialog(Frame parent) {
        JOptionPane.showMessageDialog(parent,
                "No new signature files are available.",
                SIGNATURE_UPDATE,
                JOptionPane.INFORMATION_MESSAGE);

    }
    
    /**
     * Shows an error message dialog.
     * @param parent the parent
     * @param e the exception that caused the message
     */
    public static void showSignatureUpdateErrorDialog(Frame parent, Throwable e) {
        JOptionPane.showMessageDialog(parent, e.getLocalizedMessage(), SIGNATURE_UPDATE, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an error message dialog.
     * @param parent the parent
     * @param title the error dialog title
     * @param message the message
     */
    public static void showGeneralErrorDialog(Frame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
