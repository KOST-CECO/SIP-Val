/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.results.handlers;

/**
 * @author rflitcroft
 *
 */
public interface ProgressObserver {

    /**
     * Invoked when there is progress to report.
     * @param progress an integer indicating progress (e.g. percentage).
     */
    void onProgress(Integer progress);
}
