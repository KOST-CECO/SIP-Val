/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

/**
 * A collection of identification results.
 * @author rflitcroft
 *
 */
public class IdentificationResultCollection {

    private long durationMillis;
    private Collection<IdentificationResult> results = new ArrayList<IdentificationResult>();
    private URI resourceUri;
    private Long fileLength;
    private Long correlationId;
    private long startTime;
    private boolean archive;
    private RequestMetaData requestMetaData;
    
    /**
     * 
     * @param request the original request.
     */
    public IdentificationResultCollection(IdentificationRequest request) {
        correlationId = request.getIdentifier().getParentId();
        resourceUri = request.getIdentifier().getUri();
    }

    /**
     * Adds a result.
     * @param result the result to add
     */
    public void addResult(IdentificationResult result) {
        results.add(result);
    }
    
    /**
     * 
     * @return a Collection of all results added.
     */
    public Collection<IdentificationResult> getResults() {
        return results;
    }
    
    /**
     * @return duraton of the identification in millseconds.
     */
    public long getDurationMillis() {
        return durationMillis;
    }

    /**
     * @return the jobCorrelationId
     */
    public Long getCorrelationId() {
        return correlationId;
    }

    /**
     * The URI of the request.
     * @param uri the uri of the request
     */
    public void setUri(URI uri) {
        resourceUri = uri;
    }

    /**
     * 
     * @return the URI of the request
     */
    public URI getUri() {
        return resourceUri;
    }
    
    /**
     * The file length of the resource.
     * @param fileLength the length of the file
     */
    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }
    
    /**
     * @return The file lenghth of the resource
     */
    public Long getFileLength() {
        return fileLength;
    }
    
    /**
     * Called to start the identification timing.
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Called to stop the timing of the identification.
     */
    public void stop() {
        durationMillis = System.currentTimeMillis() - startTime;
    }

    /**
     * @param archive true if the identification idicated an archive format; false otherwise
     */
    public void setArchive(boolean archive) {
        this.archive = archive;
    }
    
    /**
     * @return the archive
     */
    public boolean isArchive() {
        return archive;
    }
    
    /**
     * @param requestMetaData the requestMetaData to set
     */
    public void setRequestMetaData(RequestMetaData requestMetaData) {
        this.requestMetaData = requestMetaData;
    }
    
    /**
     * @return the requestMetaData
     */
    public RequestMetaData getRequestMetaData() {
        return requestMetaData;
    }
    
}
