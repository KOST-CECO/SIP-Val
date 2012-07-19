/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.treemodel;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.netbeans.swing.outline.RenderDataProvider;

import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;

/**
 * Proived rendering data for the profile results table.
 * @author rflitcroft
 *
 */
public class ProfileResultsRenderData implements RenderDataProvider {

    /**
     * Returns null always.
     * @param o the object to get the background colour for.
     * @return null always
     */
    @Override
    public Color getBackground(Object o) {
        return null;
    }

    /**
     * Returns the fully-qualified path of a node which is a root node, and the
     * name of any node which is not.
     * @param node the node
     * @return the display name
     */
    @Override
    public String getDisplayName(Object node) {

        ProfileResourceNode profileNode = (ProfileResourceNode) ((DefaultMutableTreeNode) node)
            .getUserObject();
        return profileNode.getMetaData().getName();
        
    }

    /**
     * Gets the foreground colour for the node given.
     * @param node the node to get the colour for
     * @return the colour for the node.
     * 
     */
    @Override
    public Color getForeground(Object node) {
        return null;
    }

    /**
     * Gets the correct icon for the node.
     * @param node the node
     * @return the icon fo the node
     */
    @Override
    public Icon getIcon(Object node) {
        
        ProfileResourceNode profileNode = (ProfileResourceNode) ((DefaultMutableTreeNode) node)
            .getUserObject();
        Icon icon = null;
        if (profileNode.allowsChildren()) {
            icon = UIManager.getIcon("Tree.closedIcon");
        } else {
            icon = UIManager.getIcon("FileView.fileIcon");
        }
        return icon;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTooltipText(Object node) {
        //ProfileResourceNode profileNode = (ProfileResourceNode) ((DefaultMutableTreeNode) node)
        //    .getUserObject();
        //return profileNode.getMetaData().getName();
        
        return null;
    }

    /**
     * @param o an object
     * @return false always
     */
    @Override
    public boolean isHtmlDisplayName(Object o) {
        return false;
    }
}
