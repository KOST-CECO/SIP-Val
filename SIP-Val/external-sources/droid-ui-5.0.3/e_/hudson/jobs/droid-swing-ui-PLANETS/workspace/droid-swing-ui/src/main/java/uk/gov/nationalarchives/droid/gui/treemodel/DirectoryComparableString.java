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
 * @author rflitcroft
 *
 */
public class DirectoryComparableString extends DirectoryComparableObject<String> {

    /**
     * 
     * @param source the source string
     * @param directory if the string represented a directory
     */
    public DirectoryComparableString(String source, boolean directory) {
        super(source, directory);
    }
}
