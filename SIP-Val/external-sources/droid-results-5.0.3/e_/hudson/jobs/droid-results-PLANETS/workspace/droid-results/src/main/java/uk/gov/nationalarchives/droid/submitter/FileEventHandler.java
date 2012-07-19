/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.submitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.core.interfaces.AsynchDroid;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationErrorType;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.ResultHandler;
import uk.gov.nationalarchives.droid.core.interfaces.archive.IdentificationRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;
import uk.gov.nationalarchives.droid.profile.throttle.SubmissionThrottle;

/**
 * @author rflitcroft
 * 
 */
public class FileEventHandler {

    private final Log log = LogFactory.getLog(getClass());

    private AsynchDroid droidCore;
    private ResultHandler resultHandler;
    private IdentificationRequestFactory requestFactory;

    private SubmissionThrottle submissionThrottle;

    /**
     * Default Constructor.
     */
    public FileEventHandler() { }
    
    
    /**
     * @param droidCore
     *            an identification engine for this event handler to submit to
     */
    public FileEventHandler(AsynchDroid droidCore) {
        this.droidCore = droidCore;
    }

    /**
     * Creates a job in the database and subimts the job to the identification
     * engine.
     * 
     * @param file
     *            the node file to handle
     * @param parentId
     *            the ID of the node's parent
     * @param nodeId
     *            an optional node ID for the request.
     */
    public void onEvent(File file, Long parentId, Long nodeId) {

        URI uri = file.toURI();
        RequestMetaData metaData = new RequestMetaData(file.length(), file
                .lastModified(),
                parentId == null ? file.getAbsolutePath() : file.getName());

        RequestIdentifier identifier = new RequestIdentifier(uri);
        identifier.setParentId(parentId);
        identifier.setNodeId(nodeId);
        
        IdentificationRequest request = requestFactory.newRequest(metaData, identifier);
        try {
            // Create a new Identification request containing the data to be
            // passed to droid core.
            FileInputStream in = new FileInputStream(file);
            request.open(in);
            log.debug(String.format(
                    "Submitting job [%s]; parent id [%s] to droid.", uri,
                    parentId));
            droidCore.submit(request);
            submissionThrottle.apply();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            resultHandler.handleError(new IdentificationException(request, IdentificationErrorType.FILE_NOT_FOUND, e));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            resultHandler.handleError(new IdentificationException(request, IdentificationErrorType.ACCESS_DENIED, e));
        } catch (InterruptedException e) {
            log.warn("Interrupted while throttle active.", e);
        }
    }

    /**
     * @return the submission throttle
     */
    public SubmissionThrottle getSubmissionThrottle() {
        return submissionThrottle;
    }
    
    /**
     * @param submissionThrottle the submissionThrottle to set
     */
    public void setSubmissionThrottle(SubmissionThrottle submissionThrottle) {
        this.submissionThrottle = submissionThrottle;
    }
    
    /**
     * @param droidCore the droidCore to set
     */
    public void setDroidCore(AsynchDroid droidCore) {
        this.droidCore = droidCore;
    }

    /**
     * @param resultHandler the resultHandler to set
     */
    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }
    
    /**
     * @param requestFactory the requestFactory to set
     */
    public void setRequestFactory(IdentificationRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

}
