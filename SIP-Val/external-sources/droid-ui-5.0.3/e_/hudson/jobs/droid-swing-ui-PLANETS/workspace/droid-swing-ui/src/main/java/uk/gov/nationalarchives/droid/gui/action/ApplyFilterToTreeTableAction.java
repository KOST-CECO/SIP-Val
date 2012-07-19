/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.action;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.netbeans.swing.outline.Outline;

import uk.gov.nationalarchives.droid.gui.DroidUIContext;
import uk.gov.nationalarchives.droid.gui.ProfileForm;
import uk.gov.nationalarchives.droid.profile.ProfileManager;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;

/**
 * Worker to apply filter.
 * @author rflitcroft
 *
 */
public class ApplyFilterToTreeTableAction {

    private DroidUIContext droidContext;
    private ProfileManager profileManager;

    /**
     * 
     * @param context the droid ui context
     * @param profileManager the profile manager
     */
    public ApplyFilterToTreeTableAction(DroidUIContext context,
            ProfileManager profileManager) {
        this.droidContext = context;
        this.profileManager = profileManager;
    }

    /**
     * Applies the filter.
     */
    public void applyFilter() {

        ProfileForm selectedProfile = droidContext.getSelectedProfile();
        DefaultTreeModel treeMdl = (DefaultTreeModel) selectedProfile
                .getTreeModel();

        Outline outline = selectedProfile.getResultsOutline();
        for (int i = outline.getRowCount(); i > 0; i--) {
            // remove node from profile spec
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) outline.getValueAt(i - 1, 0);
            if (!node.isRoot()) {
                treeMdl.removeNodeFromParent(node);
            }
        }
        treeMdl.reload();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeMdl.getRoot();

        String profileId = droidContext.getSelectedProfile().getProfile().getUuid();

        List<ProfileResourceNode> childNodes = profileManager.findRootNodes(profileId);

        for (ProfileResourceNode profileNode : childNodes) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(profileNode);
            treeNode.setAllowsChildren(profileNode.allowsChildren());
            rootNode.add(treeNode);
        }
        treeMdl.reload();

    }

}
