/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTable;

import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

import uk.gov.nationalarchives.droid.gui.action.CloseProfileAction;
import uk.gov.nationalarchives.droid.gui.action.SaveProfileWorker;
import uk.gov.nationalarchives.droid.gui.treemodel.DefaultMutableTreeNodeComparator;
import uk.gov.nationalarchives.droid.gui.treemodel.ExpandingTreeListener;
import uk.gov.nationalarchives.droid.gui.treemodel.OutlineColumn;
import uk.gov.nationalarchives.droid.gui.treemodel.OutlineComparableComparator;
import uk.gov.nationalarchives.droid.gui.treemodel.ProfileResultsRenderData;
import uk.gov.nationalarchives.droid.gui.treemodel.ProfileRowModel;
import uk.gov.nationalarchives.droid.gui.widgetwrapper.FileChooserProxy;
import uk.gov.nationalarchives.droid.gui.widgetwrapper.FileChooserProxyImpl;
import uk.gov.nationalarchives.droid.gui.widgetwrapper.JOptionPaneProxy;
import uk.gov.nationalarchives.droid.gui.worker.DroidJob;
import uk.gov.nationalarchives.droid.profile.ProfileEventListener;
import uk.gov.nationalarchives.droid.profile.ProfileInstance;
import uk.gov.nationalarchives.droid.profile.ProfileManager;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;

/**
 * 
 * @author rflitcroft
 */
public class ProfileForm extends JPanel {

    private static final long serialVersionUID = 1671584434169040994L;

    private DefaultTreeModel treeModel;
    private OutlineModel mdl;
    private ProfileInstance profile;
    private DroidMainFrame droidMainUi;
    private DroidUIContext context;
    private ProfileEventListener listener;
    private ProfileTabComponent profileTab;
    private DroidJob job;

    private final String columnNameForPUID = "PUID";
    private final String columnNameForResource = "Resource";

    private final String puidValuePrefix = "<html><a href=\"\">";
    private final String puidValueSuffix = "</a></html>";

    private Map<Long, DefaultMutableTreeNode> inMemoryNodes = new HashMap<Long, DefaultMutableTreeNode>();

    /**
     * 
     * @param droidMainUi
     *            the droid ui frame
     * @param context
     *            the droid ui context
     * @param listener
     *            a profile event listener
     */
    public ProfileForm(DroidMainFrame droidMainUi, DroidUIContext context, ProfileEventListener listener) {
        this.droidMainUi = droidMainUi;
        this.context = context;
        this.listener = listener;
        initComponents();
        // final NodeSelectionListener nodeRefreshListener =
        // new NodeSelectionListener(context, droidMainUi.getProfileManager());
        // getResultsOutline().getSelectionModel().addListSelectionListener(nodeRefreshListener);
        // getResultsOutline().addKeyListener(nodeRefreshListener);
        profileTab = new ProfileTabComponent(this);
        initOutline();
    }

    /**
     * 
     * @param droidMainUi
     *            the droidf main ui frame
     * @param context
     *            the droid ui context
     * @param profile
     *            a profile instance
     * @param listener
     *            a profile event listener
     */
    public ProfileForm(DroidMainFrame droidMainUi, DroidUIContext context, ProfileInstance profile,
            ProfileEventListener listener) {
        this(droidMainUi, context, listener);
        this.profile = profile;
    }

    private void initOutline() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(null, true);

        treeModel = new DefaultTreeModel(root, true);
        mdl = DefaultOutlineModel.createOutlineModel(treeModel, new ProfileRowModel(), true, columnNameForResource);
        resultsOutline.addMouseListener(new OutlineMouseAdapter());
        resultsOutline.addMouseMotionListener(new OutlineMouseMotionListner());
        resultsOutline.setVisible(true);
        resultsOutline.setRenderDataProvider(new ProfileResultsRenderData());
        resultsOutline.setRootVisible(false);

        TreeWillExpandListener expandingTreeListener = new ExpandingTreeListener(droidMainUi.getProfileManager(), this);
        mdl.getTreePathSupport().addTreeWillExpandListener(expandingTreeListener);

        resultsOutline.setModel(mdl);
        TableColumnModel columnModel = resultsOutline.getColumnModel();

        ETableColumn nodeColumn0 = (ETableColumn) columnModel.getColumn(0);
        nodeColumn0.setNestedComparator(new DefaultMutableTreeNodeComparator(nodeColumn0));

        OutlineColumn[] columns = OutlineColumn.values();
        for (int i = 0; i < columns.length; i++) {
            ETableColumn nodeColumn = (ETableColumn) columnModel.getColumn(i + 1);
            nodeColumn.setNestedComparator(new OutlineComparableComparator(nodeColumn));
        }

        // VITAL! We do not want to recreate columns after we have set them up
        // with their comparators!
        resultsOutline.setAutoCreateColumnsFromModel(false);
        // ((DefaultTreeModel) treeModel).reload();

        jScrollPane1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (resultsOutline.getPreferredSize().width <= jScrollPane1.getViewport().getExtentSize().width) {
                    resultsOutline.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                } else {
                    resultsOutline.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        resultsOutline = new org.netbeans.swing.outline.Outline();
        jPanel3 = new javax.swing.JPanel();
        throttlePanel = new javax.swing.JPanel();
        throttleSlider = new javax.swing.JSlider();
        throttleLabel = new javax.swing.JLabel();
        progressPanel = new javax.swing.JPanel();
        profileProgressLabel = new javax.swing.JLabel();
        profileProgressBar = new javax.swing.JProgressBar();
        statusProgressPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        statusProgressBar = new javax.swing.JProgressBar();

        resultsOutline.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        resultsOutline.setSelectVisibleColumnsLabel(org.openide.util.NbBundle.getMessage(ProfileForm.class,
                "results.columns.select")); // NOI18N
        jScrollPane1.setViewportView(resultsOutline);

        throttlePanel.setVisible(false);

        throttleSlider.setMaximum(1000);
        throttleSlider.setMinorTickSpacing(100);
        throttleSlider.setPaintTicks(true);
        throttleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                throttleSliderStateChanged(evt);
            }
        });

        throttleLabel.setLabelFor(throttleSlider);
        throttleLabel
                .setText(org.openide.util.NbBundle.getMessage(ProfileForm.class, "ProfileForm.throttleLabel.text")); // NOI18N

        javax.swing.GroupLayout throttlePanelLayout = new javax.swing.GroupLayout(throttlePanel);
        throttlePanel.setLayout(throttlePanelLayout);
        throttlePanelLayout.setHorizontalGroup(throttlePanelLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                throttlePanelLayout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(throttleLabel).addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(throttleSlider,
                        javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)));
        throttlePanelLayout.setVerticalGroup(throttlePanelLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                throttlePanelLayout.createSequentialGroup().addGroup(
                        throttlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                throttlePanelLayout.createSequentialGroup().addGap(19, 19, 19).addComponent(
                                        throttleLabel)).addGroup(
                                throttlePanelLayout.createSequentialGroup().addContainerGap().addComponent(
                                        throttleSlider, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        profileProgressLabel.setLabelFor(profileProgressBar);
        profileProgressLabel.setText(org.openide.util.NbBundle.getMessage(ProfileForm.class,
                "ProfileForm.profileProgressLabel.text")); // NOI18N

        profileProgressBar.setString(org.openide.util.NbBundle.getMessage(ProfileForm.class,
                "ProfileForm.profileProgressBar.string")); // NOI18N
        profileProgressBar.setStringPainted(true);

        javax.swing.GroupLayout progressPanelLayout = new javax.swing.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(progressPanelLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                progressPanelLayout.createSequentialGroup().addContainerGap().addComponent(profileProgressLabel)
                        .addGap(18, 18, 18).addComponent(profileProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 338,
                                Short.MAX_VALUE).addContainerGap()));
        progressPanelLayout.setVerticalGroup(progressPanelLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                progressPanelLayout.createSequentialGroup().addGap(19, 19, 19).addGroup(
                        progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(profileProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(profileProgressLabel)).addContainerGap(
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        statusLabel.setLabelFor(statusProgressBar);
        statusLabel.setText(org.openide.util.NbBundle.getMessage(ProfileForm.class, "ProfileForm.statusLabel.text")); // NOI18N

        javax.swing.GroupLayout statusProgressPanelLayout = new javax.swing.GroupLayout(statusProgressPanel);
        statusProgressPanel.setLayout(statusProgressPanelLayout);
        statusProgressPanelLayout.setHorizontalGroup(statusProgressPanelLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                statusProgressPanelLayout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addComponent(statusLabel).addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(statusProgressBar,
                        javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        statusProgressPanelLayout.setVerticalGroup(statusProgressPanelLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                statusProgressPanelLayout.createSequentialGroup().addContainerGap(24, Short.MAX_VALUE).addGroup(
                        statusProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(statusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(statusLabel))
                        .addContainerGap()));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        jPanel3Layout.createSequentialGroup().addGap(10, 10, 10).addComponent(progressPanel,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusProgressPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30).addComponent(throttlePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(progressPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                        statusProgressPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                        throttlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
                        javax.swing.GroupLayout.PREFERRED_SIZE));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()).addComponent(
                jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 528,
                        Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
    }// </editor-fold>//GEN-END:initComponents

    private void throttleSliderStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_throttleSliderStateChanged
        throttleLabel.setText(String.format("Throttle: %s ms", throttleSlider.getValue()));
        context.getProfileManager().setThrottleValue(profile.getUuid(), throttleSlider.getValue());

    }// GEN-LAST:event_throttleSliderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar profileProgressBar;
    private javax.swing.JLabel profileProgressLabel;
    private javax.swing.JPanel progressPanel;
    private org.netbeans.swing.outline.Outline resultsOutline;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JProgressBar statusProgressBar;
    private javax.swing.JPanel statusProgressPanel;
    private javax.swing.JLabel throttleLabel;
    private javax.swing.JPanel throttlePanel;
    private javax.swing.JSlider throttleSlider;

    // End of variables declaration//GEN-END:variables

    /**
     * @return the results outline
     */
    public Outline getResultsOutline() {
        return resultsOutline;
    }

    /**
     * 
     * @return the profgress bar
     */
    public JProgressBar getProfileProgressBar() {
        return profileProgressBar;
    }

    /**
     * 
     * @return The status progress bar
     */
    public JProgressBar getStatusProgressBar() {
        return statusProgressBar;
    }

    /**
     * 
     * @return the status label
     */
    public JLabel getStatusLabel() {
        return statusLabel;
    }

    /**
     * @return the treeModel
     */
    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * @param profile
     *            the profile to set
     */
    public void setProfile(ProfileInstance profile) {
        this.profile = profile;
        profile.addEventListener(listener);
        listener.fireEvent(profile);
    }

    /**
     * @return the profile
     */
    public ProfileInstance getProfile() {
        return profile;
    }

    /**
     * Closes a profile.
     */
    public void closeProfile() {
        CloseProfileAction closeAction = new CloseProfileAction(droidMainUi.getProfileManager(), context, this);
        JOptionPaneProxy dialog = new JOptionPaneProxy() {
            @Override
            public int getResponse() {
                int result = JOptionPane.showConfirmDialog(ProfileForm.this, "Save this profile?", "Warning",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                int response = JOptionPaneProxy.CANCEL;
                if (result == JOptionPane.YES_OPTION) {
                    response = JOptionPaneProxy.YES;
                } else if (result == JOptionPane.NO_OPTION) {
                    response = JOptionPaneProxy.NO;
                }

                return response;
            }
        };

        closeAction.setUserOptionDialog(dialog);
        final JFileChooser fileChooser = context.getProfileFileChooser();
        FileChooserProxy chooserProxy = new FileChooserProxyImpl(this, fileChooser);
        closeAction.setSaveAction(new SaveProfileWorker(droidMainUi.getProfileManager(), this, chooserProxy));
        closeAction.start();
    }

    /**
     * Saves a profile.
     * 
     * @param saveAs
     *            whether to show a file chooser dialog
     */
    public void saveProfile(boolean saveAs) {
        final JFileChooser fileChooser = context.getProfileFileChooser();
        fileChooser.setDialogTitle(String.format("Save profile '%s'", getName()));
        FileChooserProxy dialog = new FileChooserProxyImpl(this, fileChooser);
        File loadedFrom = getProfile().getLoadedFrom();
        fileChooser.setSelectedFile(loadedFrom != null ? loadedFrom : new File(getName()));

        SaveProfileWorker worker = new SaveProfileWorker(droidMainUi.getProfileManager(), this, dialog);
        worker.start(saveAs);
    }

    /**
     * Updates widgets before a save operation.
     */
    public void beforeSave() {
        statusLabel.setText("Saving profile...");
        statusProgressBar.setValue(0);
        statusProgressBar.setIndeterminate(false);
        statusLabel.setVisible(true);
        statusProgressBar.setVisible(true);
    }

    /**
     * Updates widgets after a save operation.
     */
    public void afterSave() {
        statusLabel.setVisible(false);
        statusProgressBar.setVisible(false);
    }

    /**
     * Updates state after loading.
     */
    public void afterLoad() {
        throttleSlider.setValue(profile.getThrottle());
        throttlePanel.setVisible(true);
        getStatusProgressBar().setVisible(false);
        getStatusLabel().setVisible(false);
        listener.fireEvent(getProfile());
    }

    /**
     * Updates state after creating new profile.
     */
    public void afterCreate() {
        throttleSlider.setValue(profile.getThrottle());
        throttlePanel.setVisible(true);
    }

    /**
     * ] Sets the state change listener.
     * 
     * @param stateChangeListener
     *            the listener to set.
     */
    public void setStateChangeListener(ProfileEventListener stateChangeListener) {
        this.listener = stateChangeListener;
    }

    /**
     * Starts a profile.
     */
    public void start() {
        ProfileManager profileManager = droidMainUi.getProfileManager();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        inMemoryNodes.put(-1L, rootNode);

        job = new DroidJob();
        job.setProfileManager(profileManager);
        job.setProfileForm(this);
        job.start();
    }

    /**
     * Stops a profile.
     */
    public void stop() {
        ProfileManager profileManager = droidMainUi.getProfileManager();
        profileManager.stop(getProfile().getUuid());
        job.cancel(true);
    }

    /**
     * @return the profileTab
     */
    public ProfileTabComponent getProfileTab() {
        return profileTab;
    }

    /**
     * @return the inMemoryNodes
     */
    public Map<Long, DefaultMutableTreeNode> getInMemoryNodes() {
        return inMemoryNodes;
    }

    /**
     * @return the throttleSlider
     */
    public JSlider getThrottleSlider() {
        return throttleSlider;
    }

    /**
     * @return the throttleLabel
     */
    JLabel getThrottleLabel() {
        return throttleLabel;
    }

    /**
     * @return the listener
     */
    public ProfileEventListener getListener() {
        return listener;
    }

    /**
     * @return the progressPanel
     */
    public JPanel getProgressPanel() {
        return progressPanel;
    }

    private String getPronumURLPrefix(String puid) {
        // get it from configuration.
        String puidUrl = droidMainUi.getGlobalContext().getGlobalConfig().getProperties().getString(
                DroidGlobalProperty.PUID_URL_PATTERN.getName());
        return String.format(puidUrl, puid);
    }

    private class OutlineMouseAdapter extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (columnNameForPUID.equals(mdl.getColumnName(resultsOutline.getSelectedColumn()))) {
                String cellValue = mdl.getValueAt(resultsOutline.getSelectedRow(), resultsOutline.getSelectedColumn())
                        .toString();
                cellValue = cellValue.replace(puidValuePrefix, "");
                cellValue = cellValue.replace(puidValueSuffix, "");
                cellValue.trim();
                if (cellValue.length() > 0) {
                    String puidUrl = getPronumURLPrefix(cellValue);
                    Desktop desktop = null;
                    if (Desktop.isDesktopSupported()) {
                        desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            try {
                                URL url = new URL(puidUrl);
                                desktop.browse(url.toURI());
                            } catch (MalformedURLException e1) {
                                DialogUtils
                                        .showGeneralErrorDialog(droidMainUi, "MalformedURLException", "Invalid URL.");
                            } catch (IOException e1) {
                                DialogUtils.showGeneralErrorDialog(droidMainUi, "IOException", "Resource not found.");
                            } catch (URISyntaxException uriSyntaxEx) {
                                DialogUtils.showGeneralErrorDialog(droidMainUi, "URISyntaxException", "Invalid URI.");
                            }
                        }
                    }
                }
            }
        }
    }

    private class OutlineMouseMotionListner implements MouseMotionListener {
        @Override
        public void mouseMoved(MouseEvent e) {

            String cellValue = resultsOutline.getValueAt(resultsOutline.rowAtPoint(e.getPoint()),
                    resultsOutline.columnAtPoint(e.getPoint())).toString();
            resultsOutline.setToolTipText(cellValue);
            if (columnNameForResource
                    .equals(resultsOutline.getColumnName(resultsOutline.columnAtPoint(e.getPoint())))) {
                ProfileResourceNode resourceNode = (ProfileResourceNode) ((DefaultMutableTreeNode) 
                        resultsOutline.getValueAt(resultsOutline.rowAtPoint(e.getPoint()),
                                resultsOutline.columnAtPoint(e.getPoint())))
                        .getUserObject();
                resultsOutline.setToolTipText(java.net.URLDecoder.decode(resourceNode.getUri().toString()));
            }
            if (columnNameForPUID.equals(resultsOutline.getColumnName(resultsOutline.columnAtPoint(e.getPoint())))) {
                cellValue = resultsOutline.getValueAt(resultsOutline.rowAtPoint(e.getPoint()),
                        resultsOutline.columnAtPoint(e.getPoint())).toString();
                cellValue = cellValue.replace(puidValuePrefix, "");
                cellValue = cellValue.replace(puidValueSuffix, "");
                cellValue.trim();
                if (cellValue.length() > 0) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub
        }
    }

}
