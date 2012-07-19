/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.treemodel;

import org.netbeans.swing.etable.ETableColumn;

/**
 * Comparator which conpares DirectoryComparable objects and always
 * sorts directories first.
 * @author rflitcroft
 *
 * @param <T>
 */
public class OutlineComparableComparator<T extends DirectoryComparable<T>> implements DirectoryComparator<T> {

    private ETableColumn column;
    
    /**
     * 
     * @param column the column which may be sorted.
     */
    public OutlineComparableComparator(ETableColumn column) {
        this.column = column;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(DirectoryComparable<T> o1, DirectoryComparable<T> o2) {
        
        int compare = o1.isFile().compareTo(o2.isFile());
        
        if (column.isAscending()) {
            compare = -compare;
        }
        
        return -(compare == 0 ? o1.compareTo(o2) : compare);
    };

}


