/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.treemodel;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 
 * @author rflitcroft
 *
 * @param <T> the source comparable
 */
public abstract class DirectoryComparableObject<T extends Comparable<T>> implements DirectoryComparable<T> {

    private T source;
    private boolean file;

    /**
     * 
     * @param source the source object
     * @param directory whether the source was for a directory
     */
    public DirectoryComparableObject(T source, boolean directory) {
        this.source = source;
        this.file = !directory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(DirectoryComparable<T> o) {
        return nullSafeCompareTo(o);
    }

    private int nullSafeCompareTo(DirectoryComparable<T> o) {
        int compare;
        
        if (source == null && o == null) {
            compare = 0;
        } else if (source == null) {
            compare = -1;
        } else if (o == null || o.getSource() == null) {
            compare = 1;
        } else {
            compare = source.compareTo(o.getSource());
        }

        return compare;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return source == null ? "" : source.toString();
    }

    /**
     * {@inheritDoc}
     * @see uk.gov.nationalarchives.droid.gui.treemodel.DirectoryComparable#getSource()
     */
    @Override
    public T getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isFile() {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(source).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        DirectoryComparableObject<T> other = (DirectoryComparableObject<T>) obj;
        return new EqualsBuilder().append(source, other.source).isEquals();
    }


}
