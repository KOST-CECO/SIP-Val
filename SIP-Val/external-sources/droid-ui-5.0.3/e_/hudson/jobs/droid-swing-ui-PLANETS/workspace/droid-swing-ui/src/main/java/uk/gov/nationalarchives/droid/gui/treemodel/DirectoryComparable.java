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
 * Extends the comparable interface to compare a class with another DirectoryComparable.
 * @author rflitcroft
 * @param <T> the source type to be compared.
 */
public interface DirectoryComparable<T> extends Comparable<DirectoryComparable<T>> {

    /**
     * 
     * @return true of the comparable represents a file, false if it represents a directory.
     */
    Boolean isFile();
    
    /**
     * 
     * @return the source object.
     */
    T getSource();
}
