/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.worker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import uk.gov.nationalarchives.droid.gui.ProfileForm;
import uk.gov.nationalarchives.droid.gui.util.DroidStringUtils;
import uk.gov.nationalarchives.droid.profile.ProfileManager;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;
import uk.gov.nationalarchives.droid.profile.ProfileResultObserver;
import uk.gov.nationalarchives.droid.results.handlers.ProgressObserver;

/**
 * 
 * @author Alok Kumar Dash
 */
public class DroidJob extends SwingWorker<Integer, ProfileResourceNode> {
    
    /** */
    private static final int RESULT_MAX_LENGTH = 60;
    /** */
    private static final int RESULT_LEFT_MIN = 20;
    private ProfileForm profileForm;
    private ProfileManager profileManager;
    private DefaultTreeModel treeModel;
    
    /**
     * 
     * @param profileManager a profile manager.
     */
    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(List<ProfileResourceNode> chunks) {
        for (ProfileResourceNode node : chunks) {
        
            Long parentId = node.getParent() == null ? -1L : node.getParent().getId();
            DefaultMutableTreeNode parent = profileForm.getInMemoryNodes().get(parentId);
            if (parent != null) {
                parent.setAllowsChildren(true);
                boolean updated = false;
                for (Enumeration<DefaultMutableTreeNode> e = parent.children(); 
                    e.hasMoreElements() && !updated;) {
                    DefaultMutableTreeNode childNode = e.nextElement();
                    if (childNode.getUserObject().equals(node)) {
                        childNode.setUserObject(node);
                        childNode.setAllowsChildren(node.allowsChildren());
                        treeModel.nodeChanged(childNode);
                        updated = true;
                    }
                } 
                
                if (!updated) {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(node, node.allowsChildren());
                    treeModel.insertNodeInto(newNode, parent, parent.getChildCount());
                }
                treeModel.nodeChanged(parent);
            }
        }
        
        if (!chunks.isEmpty()) {
            String abbreviatedUri = DroidStringUtils.abbreviate(chunks.get(0).getUri().toString(), 
                    RESULT_LEFT_MIN, RESULT_MAX_LENGTH);
            profileForm.getProfileProgressBar().setString(abbreviatedUri);
        }
    }

    @Override
    protected Integer doInBackground() {

        treeModel = (DefaultTreeModel) profileForm.getTreeModel();
        
        ProfileResultObserver myObserver = new ProfileResultObserver() {
            @Override
            public void onResult(ProfileResourceNode result) {
                publish(result);
            }
        };
        
        String profileUuid = profileForm.getProfile().getUuid();
        
        final ProgressObserver observer = new ProgressObserver() {
            @Override
            public void onProgress(Integer progress) {
                setProgress(progress);
            }
        };
        
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    Integer value = (Integer) evt.getNewValue();
                    profileForm.getProfileProgressBar().setValue(value);
                }
            }
        });
        
        profileManager.setResultsObserver(profileUuid, myObserver);
        profileManager.setProgressObserver(profileUuid, observer);

        try {
            profileManager.start(profileUuid).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause().getMessage(), e.getCause());
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    protected void done() {
        try {
            if (!isCancelled()) {
                get();
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause().getMessage(), e.getCause());
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Sets the profile form.
     * @param profileForm the profile form to set
     */
    public void setProfileForm(ProfileForm profileForm) {
        this.profileForm = profileForm;
    }

    /**
     * Starts the job.
     */
    public void start() {
        execute();
    }
    
}
