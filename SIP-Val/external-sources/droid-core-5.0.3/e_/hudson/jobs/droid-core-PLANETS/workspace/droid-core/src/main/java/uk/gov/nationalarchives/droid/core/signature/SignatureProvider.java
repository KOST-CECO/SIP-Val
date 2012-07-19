/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.signature;

import java.util.Iterator;

/**
 * Interface that the identification engine calls to read signatures.
 * @author rflitcroft
 *
 */
public interface SignatureProvider {

    /**
     * Returns an iterator over all signatures.
     * @return an itertaor over all signatures
     */
    Iterator<BinarySignature> signatureIterator();
}
