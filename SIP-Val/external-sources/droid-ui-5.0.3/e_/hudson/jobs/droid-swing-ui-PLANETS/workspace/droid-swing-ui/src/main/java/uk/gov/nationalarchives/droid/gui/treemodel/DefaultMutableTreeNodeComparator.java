/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.treemodel;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.netbeans.swing.etable.ETableColumn;

import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;

/**
 * Comparator for ProfileResourceNodes for tree table use.
 * INCONSISTENT WITH EQUALS!!! DO NOT USE THIS TO ORDER A SET OR HASHMAP! 
 * @author rflitcroft
 *
 */
public class DefaultMutableTreeNodeComparator implements Comparator<DefaultMutableTreeNode> {

    private ETableColumn column;
    
    /**
     * 
     * @param column the sortable column
     */
    public DefaultMutableTreeNodeComparator(ETableColumn column) {
        this.column = column;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {

        ProfileResourceNode f1 = (ProfileResourceNode) o1.getUserObject();
        ProfileResourceNode f2 = (ProfileResourceNode) o2.getUserObject();
        
        Boolean f1File = !f1.allowsChildren();
        Boolean f2File = !f2.allowsChildren();
//        Boolean f1File = !f1.getMetaData().getResourceType().equals(ResourceType.FOLDER);
//        Boolean f2File = !f2.getMetaData().getResourceType().equals(ResourceType.FOLDER);
        
        int dirCompare = f1File.compareTo(f2File);
        if (column.isAscending()) {
            dirCompare = -dirCompare;
        } 
        
        return -(dirCompare == 0 ? f1.getMetaData().getName().compareTo(f2.getMetaData().getName()) : dirCompare);
    }
}
