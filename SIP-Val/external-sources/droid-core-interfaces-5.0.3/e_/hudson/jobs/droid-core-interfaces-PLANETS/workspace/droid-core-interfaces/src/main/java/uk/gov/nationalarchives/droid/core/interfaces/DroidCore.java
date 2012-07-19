/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces;



/**
 * DROID core API.
 * Call submit() to submit identification requests.
 * Identification results are notified to an optional listener. This is most useful when calling the asynchronous
 * submitAsync method.
 * @author rflitcroft
 *
 */
public interface DroidCore {

//    /**
//     * Submits an identification request. This is an asynchronous operation which will
//     * return before the idenification is completed.
//     * The returned status will indicate that the request was successfully received or otherwise.
//     * @param request the identification request
//     * @return a future containing a Collection of Identification results
//     */
//    Future<IdentificationResultCollection> submitAsync(IdentificationRequest request);
    
    /**
     * Submits an identification request. This is an synchronous operation which 
     * will block until the result is returned.
     * @param request the identification request
     * @return the idenfication result
     */
    IdentificationResultCollection submit(IdentificationRequest request);

    /**
     * Sets the dignature file for the DROID core to use.
     * @param sigFilename the signature file to use
     */
    void setSignatureFile(String sigFilename);
    
//    /**
//     * Blocks until the Droid Core has no in-flight jobs.
//     * @throws InterruptedException if the thread was interrrupoted while blocked
//     */
//    void awaitIdle() throws InterruptedException;
    
}
