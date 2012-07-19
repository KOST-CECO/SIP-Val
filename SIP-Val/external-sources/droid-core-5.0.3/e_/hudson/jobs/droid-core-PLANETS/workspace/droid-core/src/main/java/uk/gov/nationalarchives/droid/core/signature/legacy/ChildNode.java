/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.signature.legacy;

/**
 * @author rflitcroft
 * @param <P> the parent type
 */
public interface ChildNode<P> {

    /**
     * Adds this node to a a parent.
     * @param parent the parent
     */
    void addToParent(P parent);
}
