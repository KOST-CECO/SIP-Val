/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.archive;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import uk.gov.nationalarchives.droid.core.interfaces.AsynchDroid;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultImpl;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.ResultHandler;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;


/**
 * @author rflitcroft
 *
 */
public class ZipArchiveHandler implements ArchiveHandler {

    private AsynchDroid droidCore;
    private IdentificationRequestFactory factory;
    private ResultHandler resultHandler;
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(IdentificationRequest request) throws IOException {

        InputStream in = request.getSourceInputStream(); 
        
        final ZipArchiveInputStream zin = new ZipArchiveInputStream(in);
        
        
        Iterable<ZipArchiveEntry> iterable = new Iterable<ZipArchiveEntry>() {
            @Override
            public Iterator<ZipArchiveEntry> iterator() {
                return new ZipInputStreamInterator(zin);
            }
        };
         

        ZipArchiveWalker walker = new ZipArchiveWalker(zin, request.getIdentifier());  
        walker.walk(iterable);
    }

    /**
     * @param parentName
     * @param entry
     * @param entryName
     * @param correlationId
     * @return
     */
    private long submitDirectory(final URI parentName,
            ZipArchiveEntry entry, String entryName, Long correlationId) {
        IdentificationResultImpl result = new IdentificationResultImpl();
        
        long size = entry.getSize();
        long time = entry.getTime();
        
        RequestMetaData metaData = new RequestMetaData(
                size != -1 ? size : null, 
                time != -1 ? time : null,
                entryName);
        
        RequestIdentifier identifier = new RequestIdentifier(
                ArchiveFileUtils.toZipUri(parentName, entry.getName()));
        
        result.setRequestMetaData(metaData);
        result.setIdentifier(identifier);
        long dirId = resultHandler.handleDirectory(result, correlationId, false);
        return dirId;
    }
    
    /**
     * Submits a request to droid.
     * @param entry the zip entry to submit
     * @param parentName the name of the parent file
     * @param entryName the name of the Zip entry
     * @param in the zip input stream
     * @param correlationId an ID to correlate this submission to
     * @param originatorNodeId the ID of the originator node
     * @throws IOException if there was an error accessing the input stream 'in'
     */
    void submit(ZipArchiveEntry entry, String entryName, URI parentName, 
            ZipArchiveInputStream in, long correlationId, long originatorNodeId) 
        throws IOException {
        
        long size = entry.getSize();
        long time = entry.getTime();
        
        RequestMetaData metaData = new RequestMetaData(
                size != -1 ? size : null, 
                time != -1 ? time : null,
                entryName);
        
        RequestIdentifier identifier = new RequestIdentifier(ArchiveFileUtils.toZipUri(parentName, entry.getName()));
        identifier.setAncestorId(originatorNodeId);
        identifier.setParentId(correlationId);
        
        IdentificationRequest request = factory.newRequest(metaData, identifier);
        request.open(in);
        droidCore.submit(request);
    }
    
    /**
     * @param factory the factory to set
     */
    public void setFactory(IdentificationRequestFactory factory) {
        this.factory = factory;
    }

    /**
     * @param droidCore the droidCore to set
     */
    public void setDroidCore(AsynchDroid droidCore) {
        this.droidCore = droidCore;
    }
    
    /**
     * Adapts an enumeration to the Iterator interface.
     * @author rflitcroft
     *
     */
    private static final class ZipInputStreamInterator 
        extends ArchiveInputStreamIterator<ZipArchiveEntry, ZipArchiveInputStream> { 
        
        /**
         * @param in
         */
        public ZipInputStreamInterator(ZipArchiveInputStream in) {
            super(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ZipArchiveEntry getNextEntry(ZipArchiveInputStream stream) throws IOException {
            return getInputStream().getNextZipEntry();
        }
    }
    
    /**
     * Archive walker for zip files.
     * @author rflitcroft
     *
     */
    private final class ZipArchiveWalker extends ArchiveFileWalker<ZipArchiveEntry> {
        
        private long parentId;
        private long originatorNodeId;
        private URI parentName;
        private ZipArchiveInputStream in;
        private final Map<String, Long> directories = new HashMap<String, Long>();
        
        ZipArchiveWalker(ZipArchiveInputStream in, RequestIdentifier identifier) {
            this.in = in;
            this.parentId = identifier.getNodeId();
            this.parentName = identifier.getUri();
            this.originatorNodeId = identifier.getAncestorId();
        }

        @Override
        protected void handleEntry(ZipArchiveEntry entry) throws IOException {
            // strip trailing slash
            String entryName = StringUtils.stripEnd(entry.getName(), "\\/");
            final String prefixPath = FilenameUtils.getPath(entryName);
            // fish the correlation ID out of the directories encountered
            
            String name;
            Long correlationId = directories.get(prefixPath);
            if (correlationId == null) {
                correlationId = parentId;
                name = entryName;
            } else {
                name = FilenameUtils.getName(entryName);
            }
            
            if (entry.isDirectory()) {
                long dirId = submitDirectory(parentName, entry, name, correlationId);
                directories.put(entry.getName(), dirId);
            } else {
                submit(entry, name, parentName, in, correlationId, originatorNodeId);
            }
        }
    }

    /**
     * @param resultHandler the resultHandler to set
     */
    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }
    
}
