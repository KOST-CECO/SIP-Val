/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.archive;

/**
 * @author rflitcroft
 *
 */
public interface ArchiveFormatResolver {

    /**
     * Resolves a PUID to its archice format.
     * @param puid the puid to resolve
     * @return an archive format
     */
    ArchiveFormat forPuid(String puid);
}
