/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.treemodel;

import java.text.NumberFormat;

/**
 * @author rflitcroft
 *
 */
public class DirectoryComparableLong extends DirectoryComparableObject<Long> {

    /**
     * @param source the source long
     * @param directory if the source represented a directory
     */
    public DirectoryComparableLong(Long source, boolean directory) {
        super(source, directory);
    }
    
    /**
     * Formats the Long as a string.
     * @return a number formatted String.
     */
    @Override
    public String toString() {
        return getSource() == null ? "" : NumberFormat.getInstance().format(getSource());
    }
}
