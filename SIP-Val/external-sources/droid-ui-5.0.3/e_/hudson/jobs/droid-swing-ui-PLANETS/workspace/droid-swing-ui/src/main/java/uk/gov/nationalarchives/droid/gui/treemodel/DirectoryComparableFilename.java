/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.treemodel;

/**
 * A DirectoryComparable which wraps a string and doers case-insensitive comparisons.
 * @author rflitcroft
 *
 */
public class DirectoryComparableFilename extends DirectoryComparableObject<String> {

    /**
     * 
     * @param source the string which will compared case-insensitively
     * @param directory whether the source object represented a directory.
     */
    public DirectoryComparableFilename(String source, boolean directory) {
        super(source, directory);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(DirectoryComparable<String> other) {
        return String.CASE_INSENSITIVE_ORDER.compare(getSource(), other.getSource());
    }
    
}
