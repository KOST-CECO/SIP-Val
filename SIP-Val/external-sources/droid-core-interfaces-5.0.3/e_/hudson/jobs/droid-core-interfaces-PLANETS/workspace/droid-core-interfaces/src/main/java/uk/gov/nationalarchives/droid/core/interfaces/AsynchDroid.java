/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces;

import java.util.concurrent.Future;


/**
 * @author rflitcroft
 *
 */
public interface AsynchDroid {

    /**
     * Submits a job asynchronously.
     * @param request the request to submit
     * @return future task
     */
    Future<IdentificationResultCollection> submit(final IdentificationRequest request);

    /**
     * Waits until in-flight jobs have finished.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    void awaitIdle() throws InterruptedException;

    /**
     * Waits until in-flight jobs have finished AND all sub-tasks have finished.
     * @throws InterruptedException if the calling thread was interrupted.
     */
    void awaitFinished() throws InterruptedException;

    /**
     * Saves the state of the droid request queue.
     */
    void save();

    /**
     * Replays any items in the droid request queue.
     */
    void replay();

}
