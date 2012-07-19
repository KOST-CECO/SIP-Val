/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui;

import java.awt.EventQueue;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.SwingHelpUtilities;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.openide.util.NbBundle;

import uk.gov.nationalarchives.droid.RuntimeConfig;
import uk.gov.nationalarchives.droid.gui.action.ActionDoneCallback;
import uk.gov.nationalarchives.droid.gui.action.ActionFactory;
import uk.gov.nationalarchives.droid.gui.action.AddFilesAndFoldersAction;
import uk.gov.nationalarchives.droid.gui.action.ApplyFilterToTreeTableAction;
import uk.gov.nationalarchives.droid.gui.action.ExitAction;
import uk.gov.nationalarchives.droid.gui.action.LoadProfileWorker;
import uk.gov.nationalarchives.droid.gui.action.NewProfileAction;
import uk.gov.nationalarchives.droid.gui.action.RemoveFilesAndFoldersAction;
import uk.gov.nationalarchives.droid.gui.action.StopRunningProfilesAction;
import uk.gov.nationalarchives.droid.gui.config.ConfigDialog;
import uk.gov.nationalarchives.droid.gui.config.SignatureUploadDialog;
import uk.gov.nationalarchives.droid.gui.config.UploadSignatureFileAction;
import uk.gov.nationalarchives.droid.gui.event.ButtonManager;
import uk.gov.nationalarchives.droid.gui.export.ExportAction;
import uk.gov.nationalarchives.droid.gui.export.ExportDialog;
import uk.gov.nationalarchives.droid.gui.export.ExportFileChooser;
import uk.gov.nationalarchives.droid.gui.filechooser.ProfileFileChooser;
import uk.gov.nationalarchives.droid.gui.filechooser.ResourceSelectorDialog;
import uk.gov.nationalarchives.droid.gui.filter.FilterDialog;
import uk.gov.nationalarchives.droid.gui.report.ReportDialog;
import uk.gov.nationalarchives.droid.gui.signature.CheckSignatureUpdateAction;
import uk.gov.nationalarchives.droid.gui.signature.UpdateSignatureAction;
import uk.gov.nationalarchives.droid.gui.widgetwrapper.ProfileSelectionDialog;
import uk.gov.nationalarchives.droid.planets.gui.PlanetXMLFileFilter;
import uk.gov.nationalarchives.droid.planets.gui.PlanetXMLProgressDialog;
import uk.gov.nationalarchives.droid.profile.FilterImpl;
import uk.gov.nationalarchives.droid.profile.ProfileManager;
import uk.gov.nationalarchives.droid.profile.ProfileManagerException;
import uk.gov.nationalarchives.droid.profile.ProfileState;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;

/**
 * @author Alok Kumar Dash
 */
public class DroidMainFrame extends JFrame {

    private static final String ERROR_TITLE = "Error";

    private static final String STATE = "state";

    private static final long serialVersionUID = 8170787911864425667L;

    private Log log = LogFactory.getLog(getClass());

    private ProfileManager profileManager;
    private DroidUIContext droidContext;
    private JFileChooser profileFileChooser = new ProfileFileChooser();
    private ResourceSelectorDialog resourceFileChooser;
    private ButtonManager buttonManager;
    private ConfigDialog configDialog;
    private GlobalContext globalContext;
    private JFileChooser exportFileChooser;
    private SignatureUploadDialog signatureUploadDialog;

    /**
     * Creates new form DroidMainFrame.
     */
    public DroidMainFrame() {
        super();
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        RuntimeConfig.configureRuntimeEnvironment();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // String os = System.getProperty("os.name").toLowerCase();
            // if (os.indexOf("windows") != -1 || os.indexOf("mac os x") != -1)
            // {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                DroidMainFrame main = new DroidMainFrame();
                main.setVisible(false);
                main.init();
                main.setVisible(true);
                main.checkSignatureUpdates();
                main.createDefaultProfile();
            }
        });
    }

    /**
     * 
     */
    void checkSignatureUpdates() {
        final Configuration properties = globalContext.getGlobalConfig().getProperties();
        boolean autoCheck = properties.getBoolean(DroidGlobalProperty.UPDATE_AUTO_CHECK.getName());
        boolean checkNow = properties.getBoolean(DroidGlobalProperty.UPDATE_ON_STARTUP.getName());

        if (autoCheck) {
            if (!checkNow) {
                long lastUpdated = properties.getLong(DroidGlobalProperty.LAST_UPDATE_CHECK.getName());
                int updateInterval = properties.getInt(DroidGlobalProperty.UPDATE_FREQUENCY_DAYS.getName());
                DateTime lastUpdateTime = new DateTime(lastUpdated);
                checkNow = lastUpdateTime.plusDays(updateInterval).isBeforeNow();
            }

            if (checkNow) {
                SignatureFileInfo availableUpdate = null;
                final ActionFactory actionFactory = globalContext.getActionFactory();
                final CheckSignatureUpdateAction checkUpdatedSignatureAction = actionFactory
                        .newCheckSignatureUpdateAction();

                checkUpdatedSignatureAction.start(this);
                availableUpdate = checkUpdatedSignatureAction.getSignatureFileInfo();

                // do the download, prompting if necesssary
                if (!checkUpdatedSignatureAction.hasError() && availableUpdate != null) {
                    boolean showPrompt = properties.getBoolean("update.downloadPrompt");
                    if (!showPrompt || (showPrompt && promptForUpdate(availableUpdate))) {
                        UpdateSignatureAction downloadAction = actionFactory.newSignaureUpdateAction();
                        downloadAction.start(this);
                    }
                }
            }
        }
    }

    /**
     * @param newSignaureUpdateAction
     */
    private boolean promptForUpdate(final SignatureFileInfo signatureFileInfo) {
        final int newVersion = signatureFileInfo.getVersion();
        return DialogUtils.showUpdateAvailableDialog(this, newVersion) == JOptionPane.YES_OPTION;
    }

    private void createDefaultProfile() {
        NewProfileAction newProfileAction = new NewProfileAction(droidContext, profileManager, jProfilesTabbedPane);
        final ProfileForm profileForm = new ProfileForm(this, droidContext, buttonManager);
        try {
            newProfileAction.init(profileForm);
            newProfileAction.execute();
        } catch (ProfileManagerException e) {
            DialogUtils.showGeneralErrorDialog(this, ERROR_TITLE, e.getMessage());
        }
    }

    private void init() {
        URL icon = getClass().getResource("/uk/gov/nationalarchives/droid/icons/DROID16.gif");
        setIconImage(new ImageIcon(icon).getImage());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        initComponents();
        setLocationRelativeTo(null);

        // 1. create HelpSet and HelpBroker objects
        try {
            SwingHelpUtilities.setContentViewerUI("uk.gov.nationalarchives.droid.gui.help.ExternalLinkContentViewerUI");
            
            HelpSet hs = getHelpSet("helpset.hs");
            HelpBroker hb = hs.createHelpBroker();


            // 2. assign help to components
            CSH.setHelpIDString(helpMenuItem, "Welcome to DROID");

            // 3. handle events
            helpMenuItem.addActionListener(new CSH.DisplayHelpFromSource(hb));
        } catch (HelpSetException e) {
            log.error(e);
        }

        globalContext = new SpringGuiContext();
        profileManager = globalContext.getProfileManager();
        configDialog = new ConfigDialog(this, globalContext);
        droidContext = new DroidUIContext(jProfilesTabbedPane, profileManager);
        exportFileChooser = new ExportFileChooser();
        signatureUploadDialog = new SignatureUploadDialog(this);
        resourceFileChooser = new ResourceSelectorDialog(this);
        resourceFileChooser.setModal(true);

        initButtons();
    }

    /**
     * Find the helpset file and create a HelpSet object.
     */
    private HelpSet getHelpSet(String helpsetfile) throws HelpSetException {
        HelpSet hs = null;
        ClassLoader cl = this.getClass().getClassLoader();
        URL hsURL = HelpSet.findHelpSet(cl, helpsetfile);
        hs = new HelpSet(null, hsURL);
        return hs;
    }

    private void initButtons() {
        buttonManager = new ButtonManager(droidContext);
        buttonManager.addCreateComponent(jButtonNewProfile);
        buttonManager.addCreateComponent(jMenuItemNew);

        buttonManager.addLoadComponent(jButtonOpenProfile);
        buttonManager.addLoadComponent(jMenuItemOpen);

        buttonManager.addSaveComponent(jButtonSaveProfile);
        buttonManager.addSaveComponent(jMenuSave);

        buttonManager.addRunComponent(jButtonStart);
        buttonManager.addRunComponent(jMenuItemStart);

        buttonManager.addStopComponent(jButtonStop);
        buttonManager.addStopComponent(jMenuItemStop);

        buttonManager.addResourceComponent(jButtonAddFile);
        buttonManager.addResourceComponent(jButtonRemoveFilesAndFolder);
        buttonManager.addResourceComponent(jMenuItemAddFileOrFolders);
        buttonManager.addResourceComponent(jMenuItemRemoveFolder);

        buttonManager.addExportComponent(jMenuItemExport);
        buttonManager.addExportComponent(jButtonExport);

        buttonManager.addFilterComponent(jMenuEditFilter);
        buttonManager.addFilterComponent(filterEnabledMenuItem);
        buttonManager.addFilterComponent(jButtonFilter);
        buttonManager.addFilterComponent(jMenuFilter);
        buttonManager.addFilterComponent(jMenuItemCopyFilterToAll);

        buttonManager.addReportComponent(jMenuPlanetsXML);
        // buttonManager.addReportComponent(jButtonReport);

        buttonManager.fireEvent(null);
    }

    // </editor-fold>
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProfilesTabbedPane = new javax.swing.JTabbedPane();
        droidToolBar = new javax.swing.JToolBar();
        jButtonNewProfile = new javax.swing.JButton();
        jButtonOpenProfile = new javax.swing.JButton();
        jButtonSaveProfile = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonAddFile = new javax.swing.JButton();
        jButtonRemoveFilesAndFolder = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButtonStart = new javax.swing.JButton();
        jButtonStop = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonFilter = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButtonExport = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuSave = new javax.swing.JMenuItem();
        jMenuItemExport = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemAddFileOrFolders = new javax.swing.JMenuItem();
        jMenuItemRemoveFolder = new javax.swing.JMenuItem();
        jMenuRun = new javax.swing.JMenu();
        jMenuItemStart = new javax.swing.JMenuItem();
        jMenuItemStop = new javax.swing.JMenuItem();
        jMenuFilter = new javax.swing.JMenu();
        filterEnabledMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        jMenuEditFilter = new javax.swing.JMenuItem();
        jMenuItemCopyFilterToAll = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jSeparator7 = new javax.swing.JSeparator();
        updateNowMenuItem = new javax.swing.JMenuItem();
        signatureUploadMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        settingsMenuItem = new javax.swing.JMenuItem();
        jMenuReport = new javax.swing.JMenu();
        jMenuPlanetsXML = new javax.swing.JMenuItem();
        jhelp = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(DroidMainFrame.class, "main.title")); // NOI18N

        jProfilesTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jProfilesTabbedPane.setAutoscrolls(true);
        jProfilesTabbedPane.setMaximumSize(null);
        jProfilesTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jProfilesTabbedPaneStateChanged(evt);
            }
        });

        droidToolBar.setFloatable(false);
        droidToolBar.setRollover(true);
        droidToolBar.setMargin(new java.awt.Insets(10, 10, 10, 10));
        droidToolBar.setMaximumSize(new java.awt.Dimension(0, 0));
        droidToolBar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                droidToolBarPropertyChange(evt);
            }
        });

        jButtonNewProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/New.png"))); // NOI18N
        jButtonNewProfile.setText("New");
        jButtonNewProfile.setToolTipText("Create new profile");
        jButtonNewProfile.setFocusable(false);
        jButtonNewProfile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewProfile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewProfileActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonNewProfile);

        jButtonOpenProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Open file.png"))); // NOI18N
        jButtonOpenProfile.setText("Open");
        jButtonOpenProfile.setToolTipText("Open existing profile");
        jButtonOpenProfile.setFocusable(false);
        jButtonOpenProfile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpenProfile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpenProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenProfileActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonOpenProfile);

        jButtonSaveProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Save.png"))); // NOI18N
        jButtonSaveProfile.setText("Save");
        jButtonSaveProfile.setToolTipText("Save profile");
        jButtonSaveProfile.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Save disabled.png"))); // NOI18N
        jButtonSaveProfile.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Save disabled.png"))); // NOI18N
        jButtonSaveProfile.setFocusable(false);
        jButtonSaveProfile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSaveProfile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSaveProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveProfileActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonSaveProfile);

        jSeparator1.setAlignmentX(2.0F);
        droidToolBar.add(jSeparator1);

        jButtonAddFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Add.png"))); // NOI18N
        jButtonAddFile.setText("Add");
        jButtonAddFile.setToolTipText("Add files/folders to profile");
        jButtonAddFile.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Add disabled.png"))); // NOI18N
        jButtonAddFile.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Add disabled.png"))); // NOI18N
        jButtonAddFile.setFocusable(false);
        jButtonAddFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddFile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddFileActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonAddFile);

        jButtonRemoveFilesAndFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Remove.png"))); // NOI18N
        jButtonRemoveFilesAndFolder.setText("Remove");
        jButtonRemoveFilesAndFolder.setToolTipText("Remove files/folders from profile");
        jButtonRemoveFilesAndFolder.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Remove disabled.png"))); // NOI18N
        jButtonRemoveFilesAndFolder.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Remove disabled.png"))); // NOI18N
        jButtonRemoveFilesAndFolder.setFocusable(false);
        jButtonRemoveFilesAndFolder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRemoveFilesAndFolder.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRemoveFilesAndFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveFilesAndFolderActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonRemoveFilesAndFolder);
        droidToolBar.add(jSeparator2);

        jButtonStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Play.png"))); // NOI18N
        jButtonStart.setText("Start");
        jButtonStart.setToolTipText("Run identification");
        jButtonStart.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Play disabled.png"))); // NOI18N
        jButtonStart.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Play disabled.png"))); // NOI18N
        jButtonStart.setFocusable(false);
        jButtonStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonStart);

        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Pause.png"))); // NOI18N
        jButtonStop.setText("Pause");
        jButtonStop.setToolTipText("Pause identification");
        jButtonStop.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Pause disabled.png"))); // NOI18N
        jButtonStop.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Pause disabled.png"))); // NOI18N
        jButtonStop.setFocusable(false);
        jButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonStop);
        droidToolBar.add(jSeparator3);

        jButtonFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/icons/Filter.png"))); // NOI18N
        jButtonFilter.setText("Filter");
        jButtonFilter.setToolTipText("Define and apply filter to results");
        jButtonFilter.setFocusable(false);
        jButtonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFilterActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonFilter);
        droidToolBar.add(jSeparator5);

        jButtonExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Export.png"))); // NOI18N
        jButtonExport.setText("Export");
        jButtonExport.setToolTipText("Export results");
        jButtonExport.setFocusable(false);
        jButtonExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });
        droidToolBar.add(jButtonExport);

        jMenuBar1.setMinimumSize(null);
        jMenuBar1.setPreferredSize(new java.awt.Dimension(100, 21));

        jMenuFile.setText("File");
        jMenuFile.setActionCommand("file");
        jMenuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileActionPerformed(evt);
            }
        });

        jMenuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/New small.png"))); // NOI18N
        jMenuItemNew.setText("New");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Open file small.png"))); // NOI18N
        jMenuItemOpen.setText("Open");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Save small.png"))); // NOI18N
        jMenuSave.setText("Save");
        jMenuSave.setActionCommand("save");
        jMenuSave.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/icons/Save Blue 16 d g.gif"))); // NOI18N
        jMenuSave.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/icons/Save Blue 16 d g.gif"))); // NOI18N
        jMenuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuSave);

        jMenuItemExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Export small.png"))); // NOI18N
        jMenuItemExport.setText("Export all...");
        jMenuItemExport.setActionCommand("export");
        jMenuItemExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExport);
        jMenuFile.add(jSeparator4);

        jMenuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Quit Small.png"))); // NOI18N
        jMenuExit.setText("Exit");
        jMenuExit.setActionCommand("exit");
        jMenuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuExit);

        jMenuBar1.add(jMenuFile);

        jMenuEdit.setText("Edit");
        jMenuEdit.setActionCommand("edit");

        jMenuItemAddFileOrFolders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Add small.png"))); // NOI18N
        jMenuItemAddFileOrFolders.setText("Add file/folders");
        jMenuItemAddFileOrFolders.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Add small disabled.png"))); // NOI18N
        jMenuItemAddFileOrFolders.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Add small disabled.png"))); // NOI18N
        jMenuItemAddFileOrFolders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddFileOrFoldersActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemAddFileOrFolders);

        jMenuItemRemoveFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Remove small.png"))); // NOI18N
        jMenuItemRemoveFolder.setText("Remove files/folders");
        jMenuItemRemoveFolder.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Remove small disabled.png"))); // NOI18N
        jMenuItemRemoveFolder.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Remove small disabled.png"))); // NOI18N
        jMenuItemRemoveFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoveFolderActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemRemoveFolder);

        jMenuBar1.add(jMenuEdit);

        jMenuRun.setMnemonic('j');
        jMenuRun.setText("Run");
        jMenuRun.setActionCommand("run");

        jMenuItemStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Play small.png"))); // NOI18N
        jMenuItemStart.setText("Start identification");
        jMenuItemStart.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Play small disabled.png"))); // NOI18N
        jMenuItemStart.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Play small disabled.png"))); // NOI18N
        jMenuItemStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStartActionPerformed(evt);
            }
        });
        jMenuRun.add(jMenuItemStart);

        jMenuItemStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Pause small.png"))); // NOI18N
        jMenuItemStop.setText("Pause identification");
        jMenuItemStop.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Pause small disabled.png"))); // NOI18N
        jMenuItemStop.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/gov/nationalarchives/droid/OldIcons/Pause small disabled.png"))); // NOI18N
        jMenuItemStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStopActionPerformed(evt);
            }
        });
        jMenuRun.add(jMenuItemStop);

        jMenuBar1.add(jMenuRun);

        jMenuFilter.setText("Filter");
        jMenuFilter.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenuFilterMenuSelected(evt);
            }
        });

        filterEnabledMenuItem.setText("Apply filter");
        filterEnabledMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterEnabledMenuItemActionPerformed(evt);
            }
        });
        jMenuFilter.add(filterEnabledMenuItem);
        jMenuFilter.add(jSeparator8);

        jMenuEditFilter.setText("Edit filter...");
        jMenuEditFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuEditFilterActionPerformed(evt);
            }
        });
        jMenuFilter.add(jMenuEditFilter);

        jMenuItemCopyFilterToAll.setText("Copy filter to all profiles...");
        jMenuItemCopyFilterToAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyFilterToAllActionPerformed(evt);
            }
        });
        jMenuFilter.add(jMenuItemCopyFilterToAll);

        jMenuBar1.add(jMenuFilter);

        jMenuTools.setText("Tools");
        jMenuTools.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuToolsActionPerformed(evt);
            }
        });
        jMenuTools.add(jSeparator7);

        updateNowMenuItem.setText("Check for signature updates...");
        updateNowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateNowMenuItemActionPerformed(evt);
            }
        });
        jMenuTools.add(updateNowMenuItem);

        signatureUploadMenuItem.setText("Upload signature file...");
        signatureUploadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signatureUploadMenuItemActionPerformed(evt);
            }
        });
        jMenuTools.add(signatureUploadMenuItem);
        jMenuTools.add(jSeparator6);

        settingsMenuItem.setText("Preferences...");
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        jMenuTools.add(settingsMenuItem);

        jMenuBar1.add(jMenuTools);

        jMenuReport.setText("Report");
        jMenuReport.setActionCommand("report");

        jMenuPlanetsXML.setText("Generate PLANETS XML");
        jMenuPlanetsXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuPlanetsXMLActionPerformed(evt);
            }
        });
        jMenuReport.add(jMenuPlanetsXML);

        jMenuBar1.add(jMenuReport);

        jhelp.setText("Help");
        jhelp.setActionCommand("help");

        helpMenuItem.setText("Help");
        jhelp.add(helpMenuItem);

        jMenuBar1.add(jhelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(droidToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
            .addComponent(jProfilesTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(droidToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProfilesTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuFilterMenuSelected(javax.swing.event.MenuEvent evt) {// GEN-FIRST:event_jMenuFilterMenuSelected

        FilterImpl filter = droidContext.getSelectedProfile().getProfile().getFilter();
        if (filter.getFilterCriteriaMap() == null || filter.getFilterCriteriaMap().isEmpty()) {
            filterEnabledMenuItem.setSelected(false);
            filterEnabledMenuItem.setEnabled(false);
        } else {
            filterEnabledMenuItem.setEnabled(true);
            if (filter.isEnabled()) {
                filterEnabledMenuItem.setSelected(true);
            } else {
                filterEnabledMenuItem.setSelected(false);
            }

        }

    }// GEN-LAST:event_jMenuFilterMenuSelected

    private void filterEnabledMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_filterEnabledMenuItemActionPerformed
        FilterImpl filter = droidContext.getSelectedProfile().getProfile().getFilter();
        if (filter != null) {
            filter.setEnabled(filterEnabledMenuItem.isSelected());
        }
        ApplyFilterToTreeTableAction applyFilter = new ApplyFilterToTreeTableAction(droidContext, profileManager);
        applyFilter.applyFilter();

    }// GEN-LAST:event_filterEnabledMenuItemActionPerformed

    private void signatureUploadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_signatureUploadMenuItemActionPerformed
        signatureUploadDialog.setLocationRelativeTo(this);
        signatureUploadDialog.setVisible(true);
        if (signatureUploadDialog.getResponse() == SignatureUploadDialog.OK) {
            UploadSignatureFileAction action = globalContext.getActionFactory().newUploadSignatureFileAction();
            action.setFileName(signatureUploadDialog.getSignatureFilename());
            action.setUseAsDefault(signatureUploadDialog.isDefault());
            action.execute(this);
        }

    }// GEN-LAST:event_signatureUploadMenuItemActionPerformed

    private void jMenuEditFilterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuFilterActionPerformed
        FilterImpl filter = droidContext.getSelectedProfile().getProfile().getFilter();
        FilterDialog dialog = new FilterDialog(this, true, filter, droidContext, profileManager);
        // dialog.setTitle("Filter Selection dialog.");
        dialog.setVisible(true);

    }// GEN-LAST:event_jMenuFilterActionPerformed

    private void jButtonReportActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jReportActionPerformed
        // report();
    }// GEN-LAST:event_jReportActionPerformed

    private void jButtonOpenProfileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonOpenProfileActionPerformed
        openProfileAction(evt);
    }// GEN-LAST:event_jButtonOpenProfileActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemNewActionPerformed

        jButtonNewProfileActionPerformed(evt);

    }// GEN-LAST:event_jMenuItemNewActionPerformed

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemOpenActionPerformed
        openProfileAction(evt);
    }// GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuSaveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuSaveActionPerformed
        saveProfileAction(evt);
    }// GEN-LAST:event_jMenuSaveActionPerformed

    private void jMenuItemExportActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemExportActionPerformed
        export();
    }// GEN-LAST:event_jMenuItemExportActionPerformed

    private void jMenuExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuExitActionPerformed
        exit();
    }// GEN-LAST:event_jMenuExitActionPerformed

    private void jMenuItemStartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemStartActionPerformed
        startProfile();
    }// GEN-LAST:event_jMenuItemStartActionPerformed

    private void jMenuItemStopActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemStopActionPerformed
        stopProfile();
    }// GEN-LAST:event_jMenuItemStopActionPerformed

    private void jMenuItemAddFileOrFoldersActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemAddFileOrFoldersActionPerformed

        jButtonAddFileActionPerformed(evt);

    }// GEN-LAST:event_jMenuItemAddFileOrFoldersActionPerformed

    private void jMenuItemRemoveFolderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemRemoveFolderActionPerformed

        jButtonRemoveFilesAndFolderActionPerformed(evt);

    }// GEN-LAST:event_jMenuItemRemoveFolderActionPerformed

    private void jButtonSaveProfileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSaveProfileActionPerformed
        saveProfileAction(evt);
    }// GEN-LAST:event_jButtonSaveProfileActionPerformed

    private void jProfilesTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_jProfilesTabbedPaneStateChanged
        ProfileForm profileForm = droidContext.getSelectedProfile();
        buttonManager.fireEvent(profileForm == null ? null : profileForm.getProfile());

    }// GEN-LAST:event_jProfilesTabbedPaneStateChanged

    private void droidToolBarPropertyChange(java.beans.PropertyChangeEvent evt) {// GEN-FIRST:event_droidToolBarPropertyChange

        // TODO add your handling code here:
    }// GEN-LAST:event_droidToolBarPropertyChange

    private void jMenuFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuFileActionPerformed

    }// GEN-LAST:event_jMenuFileActionPerformed

    private void jButtonFilterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFilterActionPerformed
        FilterImpl filter = droidContext.getSelectedProfile().getProfile().getFilter();
        FilterDialog dialog = new FilterDialog(this, true, filter, droidContext, profileManager);
        // dialog.setTitle("Filter Selection dialog.");
        dialog.setVisible(true);

    }// GEN-LAST:event_jButtonFilterActionPerformed

    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonExportActionPerformed
        export();
    }// GEN-LAST:event_jButtonExportActionPerformed

    private void jMenuItemCopyFilterToAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemCopyFIlterToAllActionPerformed
        FilterImpl filter = droidContext.getSelectedProfile().getProfile().getFilter();
        Collection<ProfileForm> profileForms = droidContext.allProfiles();
        for (ProfileForm profileForm : profileForms) {
            profileForm.getProfile().setFilter((FilterImpl) filter.clone());
        }

    }// GEN-LAST:event_jMenuItemCopyFIlterToAllActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_settingsMenuItemActionPerformed
        // initialise the dialog's values
        Map<String, Object> settings = globalContext.getGlobalConfig().getPropertiesMap();
        configDialog.init(settings);

        configDialog.setVisible(true);
        if (configDialog.getResponse() == ConfigDialog.OK) {
            try {
                globalContext.getGlobalConfig().update(configDialog.getGlobalConfig());
            } catch (ConfigurationException e) {
                log.error("Error updating properties: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(configDialog, NbBundle.getMessage(ConfigDialog.class,
                        "ConfigDialog.error.text"),
                        NbBundle.getMessage(ConfigDialog.class, "ConfigDialog.error.title"), JOptionPane.ERROR_MESSAGE);

            }
        }
    }// GEN-LAST:event_settingsMenuItemActionPerformed

    private void jMenuToolsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuToolsActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jMenuToolsActionPerformed

    private void updateNowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_updateNowMenuItemActionPerformed
        SignatureFileInfo availableUpdate = null;
        final ActionFactory actionFactory = globalContext.getActionFactory();
        final CheckSignatureUpdateAction checkUpdatedSignatureAction = actionFactory.newCheckSignatureUpdateAction();

        checkUpdatedSignatureAction.start(this);
        availableUpdate = checkUpdatedSignatureAction.getSignatureFileInfo();

        if (!checkUpdatedSignatureAction.hasError() && !checkUpdatedSignatureAction.isCancelled()) {
            // do the download, prompting if necesssary
            if (availableUpdate != null) {
                if (promptForUpdate(availableUpdate)) {
                    UpdateSignatureAction downloadAction = actionFactory.newSignaureUpdateAction();
                    downloadAction.start(this);
                }
            } else {
                DialogUtils.showUpdateUnavailableDialog(this);
            }
        }
    }// GEN-LAST:event_updateNowMenuItemActionPerformed

    private void jMenuPlanetsXMLActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuPlanetsXMLActionPerformed
        
        

        String profileId = droidContext.getSelectedProfile().getProfile().getUuid();

        if (droidContext.getSelectedProfile().getProfile().getState().equals(ProfileState.FINISHED)
                || droidContext.getSelectedProfile().getProfile().getState().equals(ProfileState.STOPPED)) {
            String filePath = "";
            JFileChooser c = new JFileChooser();
            c.setDialogTitle("Please provide name of the file.");
            PlanetXMLFileFilter fileFilter = new PlanetXMLFileFilter("xml", "Planet XML");
            c.addChoosableFileFilter(fileFilter);
            int rVal = c.showSaveDialog(this);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                if (fileFilter.getExtension(c.getSelectedFile()) == null) {
                    filePath = c.getCurrentDirectory().toString() + File.separator + c.getSelectedFile().getName()
                            + ".xml";
                } else {
                    filePath = c.getSelectedFile().getPath();
                }
                // if file dose not exist.
                if (!new File(filePath).isFile()) {
                    // Call PlanetXMLDIalog with the file path.
                    PlanetXMLProgressDialog planetXMLProgressDialog = new PlanetXMLProgressDialog(this, true, filePath,
                            profileId, globalContext.getReportManager());
                    planetXMLProgressDialog.show();
                    // if Overwrite option is selected.
                } else if (JOptionPane.showConfirmDialog(this, "File already exists. Do you want to overwrite.",
                        "File already exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    PlanetXMLProgressDialog planetXMLProgressDialog = new PlanetXMLProgressDialog(this, true, filePath,
                            profileId, globalContext.getReportManager());
                    planetXMLProgressDialog.setLocationRelativeTo(this);
                    planetXMLProgressDialog.show();

                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selected profile not paused or finished.", "Profile state error",
                    JOptionPane.ERROR_MESSAGE);

        }

    }// GEN-LAST:event_menuPlanetsXMLActionPerformed

    private void jButtonNewProfileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonNewProfileActionPerformed

        NewProfileAction newProfileAction = new NewProfileAction(droidContext, profileManager, jProfilesTabbedPane);
        try {
            newProfileAction.init(new ProfileForm(this, droidContext, buttonManager));
            newProfileAction.execute();
        } catch (ProfileManagerException e) {
            DialogUtils.showGeneralErrorDialog(this, ERROR_TITLE, e.getMessage());
        }

    }// GEN-LAST:event_jButtonNewProfileActionPerformed

    private void jButtonAddFileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddFileActionPerformed

        int returnVal = resourceFileChooser.showDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            AddFilesAndFoldersAction action = new AddFilesAndFoldersAction(droidContext, profileManager);
            action.add(resourceFileChooser.getSelectedFiles(), resourceFileChooser.isSelectionRecursive());
        }

    }// GEN-LAST:event_jButtonAddFileActionPerformed

    private void jButtonRemoveFilesAndFolderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonRemoveFilesAndFolderActionPerformed
        if (droidContext.getSelectedProfile().getResultsOutline().getSelectedRows().length == 0) {
            DialogUtils.showNothingIsSelectedForRemoveDialog(this);
        }
        RemoveFilesAndFoldersAction removeAction = new RemoveFilesAndFoldersAction(droidContext, profileManager);
        removeAction.remove();

    }// GEN-LAST:event_jButtonRemoveFilesAndFolderActionPerformed

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonStartActionPerformed
        startProfile();

    }// GEN-LAST:event_jButtonStartActionPerformed

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonStopActionPerformed
        stopProfile();
    }// GEN-LAST:event_jButtonStopActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar droidToolBar;
    private javax.swing.JCheckBoxMenuItem filterEnabledMenuItem;
    private javax.swing.JMenuItem helpMenuItem;
    protected javax.swing.JButton jButtonAddFile;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonFilter;
    private javax.swing.JButton jButtonNewProfile;
    protected javax.swing.JButton jButtonOpenProfile;
    protected javax.swing.JButton jButtonRemoveFilesAndFolder;
    protected javax.swing.JButton jButtonSaveProfile;
    protected javax.swing.JButton jButtonStart;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenuItem jMenuEditFilter;
    protected javax.swing.JMenuItem jMenuExit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuFilter;
    protected javax.swing.JMenuItem jMenuItemAddFileOrFolders;
    private javax.swing.JMenuItem jMenuItemCopyFilterToAll;
    protected javax.swing.JMenuItem jMenuItemExport;
    protected javax.swing.JMenuItem jMenuItemNew;
    protected javax.swing.JMenuItem jMenuItemOpen;
    protected javax.swing.JMenuItem jMenuItemRemoveFolder;
    protected javax.swing.JMenuItem jMenuItemStart;
    protected javax.swing.JMenuItem jMenuItemStop;
    private javax.swing.JMenuItem jMenuPlanetsXML;
    private javax.swing.JMenu jMenuReport;
    private javax.swing.JMenu jMenuRun;
    protected javax.swing.JMenuItem jMenuSave;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JTabbedPane jProfilesTabbedPane;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JMenu jhelp;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JMenuItem signatureUploadMenuItem;
    private javax.swing.JMenuItem updateNowMenuItem;
    // End of variables declaration//GEN-END:variables

    private void openProfileAction(ActionEvent event) {

        int result = profileFileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = profileFileChooser.getSelectedFile();

            if (!droidContext.selectProfileWithSource(selectedFile)) {
                // Give the tab with this profile the focus...
                LoadProfileWorker worker = new LoadProfileWorker(profileManager, droidContext, jProfilesTabbedPane);
                worker.setProfileFile(selectedFile);
                worker.init(new ProfileForm(this, droidContext, buttonManager));
                worker.execute();
            }
        }
    }

    private void saveProfileAction(ActionEvent event) {
        final ProfileForm profileForm = droidContext.getSelectedProfile();
        profileForm.saveProfile(false);
    }

    private void saveProfileAsAction(ActionEvent event) {
        final ProfileForm profileForm = droidContext.getSelectedProfile();
        profileForm.saveProfile(true);
    }

    private void startProfile() {
        final ProfileForm profileForm = droidContext.getSelectedProfile();

        if (profileForm.getProfile().getFilter().isEnabled()) {
            profileForm.getProfile().getFilter().setEnabled(false);
            ApplyFilterToTreeTableAction refreshTreeTable = new ApplyFilterToTreeTableAction(this.droidContext,
                    this.profileManager);
            refreshTreeTable.applyFilter();
        }
        profileForm.start();
    }

    private void stopProfile() {
        final ProfileForm profileForm = droidContext.getSelectedProfile();
        profileForm.stop();
    }

    private void report() {
        List<String> profileIds = new ArrayList<String>();
        for (ProfileForm profileForm : droidContext.allProfiles()) {
            profileIds.add(profileForm.getProfile().getUuid());
        }
        final ReportDialog reportDialog = new ReportDialog(this, profileIds, globalContext);
        reportDialog.show();
    }

    private void export() {
        int response = exportFileChooser.showSaveDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            List<String> profileIds = new ArrayList<String>();
            for (ProfileForm profileForm : droidContext.allProfiles()) {
                profileIds.add(profileForm.getProfile().getUuid());
            }
            final ExportAction exportAction = globalContext.getActionFactory().newExportAction();

            final ExportDialog exportDialog = new ExportDialog(this, exportAction);

            exportAction.setDestination(exportFileChooser.getSelectedFile());
            exportAction.setProfileIds(profileIds);

            exportAction.setCallback(new ActionDoneCallback<ExportAction>() {
                @Override
                public void done(ExportAction action) {
                    try {
                        exportDialog.setVisible(false);
                        action.get();
                        JOptionPane.showMessageDialog(DroidMainFrame.this, "Export Complete.", "Export Complete",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (ExecutionException e) {
                        DialogUtils.showGeneralErrorDialog(DroidMainFrame.this, "Export Error", e.getCause()
                                .getMessage());
                    } catch (InterruptedException e) {
                        DialogUtils.showGeneralErrorDialog(DroidMainFrame.this, "Export Interrupted", e.getCause()
                                .getMessage());
                    } catch (CancellationException e) {
                        log.info("Export cancelled");
                    }
                }
            });

            exportAction.execute();
            exportDialog.setVisible(true);
        }
    }

    private void exit() {
        StopRunningProfilesAction stopRunningAction = new StopRunningProfilesAction(profileManager, droidContext, this);
        if (stopRunningAction.execute()) {
            ProfileSelectionDialog dialog = new SaveAllProfilesDialog(this, droidContext.allDirtyProfiles());
            final ExitAction action = new ExitAction(droidContext, dialog, profileManager);
            action.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (STATE.equals(evt.getPropertyName()) && evt.getNewValue().equals(SwingWorker.StateValue.DONE)
                            && !action.isCancelled()) {
                        setVisible(true);
                        dispose();
                        // CHECKSTYLE:OFF
                        System.exit(0);
                        // CHECKSTYLE:ON
                    }
                }
            });

            action.start();
        }
    }

    /**
     * 
     * @return the profile manager
     */
    public ProfileManager getProfileManager() {
        return profileManager;
    }

    /**
     * @return the profile file chooser
     */
    public JFileChooser getProfileFileChooser() {
        return profileFileChooser;
    }

    /**
     * @return the globalContext
     */
    public GlobalContext getGlobalContext() {
        return globalContext;
    }
    
    

}
