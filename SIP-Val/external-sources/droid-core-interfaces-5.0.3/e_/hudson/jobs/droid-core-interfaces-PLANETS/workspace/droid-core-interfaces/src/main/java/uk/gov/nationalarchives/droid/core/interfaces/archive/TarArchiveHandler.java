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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
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
public class TarArchiveHandler implements ArchiveHandler {

    private AsynchDroid droidCore;
    private IdentificationRequestFactory factory;
    private ResultHandler resultHandler;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(IdentificationRequest request) throws IOException {

        InputStream tarIn = request.getSourceInputStream(); 
        URI parentName = request.getIdentifier().getUri(); 
        long parentId = request.getIdentifier().getNodeId();

        final TarArchiveInputStream in = new TarArchiveInputStream(tarIn);
        
        
        Iterable<TarArchiveEntry> iterable = new Iterable<TarArchiveEntry>() {
            @Override
            public Iterator<TarArchiveEntry> iterator() {
                return new TarArchiveEntryIterator(in);
            }
        };
        
         
        TarArchiveWalker walker = new TarArchiveWalker(in, parentId, 
                request.getIdentifier().getAncestorId(), parentName);
        walker.walk(iterable);
    }
    
    /**
     * Adapts a TarArchiveInputStream to an iterator.
     * @author rflitcroft
     *
     */
    private static final class TarArchiveEntryIterator 
        extends ArchiveInputStreamIterator<TarArchiveEntry, TarArchiveInputStream> {
        
        TarArchiveEntryIterator(TarArchiveInputStream in) {
            super(in);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected TarArchiveEntry getNextEntry(TarArchiveInputStream stream) throws IOException {
            return getInputStream().getNextTarEntry();
        }
        
    }
    
    /**
     * Submits a request to droid.
     * @param entry the tar entry to submit
     * @param entryName the name of the entry
     * @param parentName the name of the parent file
     * @param in the archive input stream
     * @param correlationId the correlation iod for the request
     * @param originatorNodeId the ID of the originator node
     * @throws IOException if the input stream could not be read
     */
    void submit(TarArchiveEntry entry, String entryName, URI parentName, 
            ArchiveInputStream in, long correlationId, long originatorNodeId) throws IOException {
        long size = entry.getSize();
        Date time = entry.getModTime();

        RequestMetaData metaData = new RequestMetaData(
                size == -1 ? null : size,
                time == null ? null : time.getTime(),
                entryName);
        
        RequestIdentifier identifier = 
            new RequestIdentifier(ArchiveFileUtils.toTarUri(parentName, entry.getName()));
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
     * @param resultHandler the resultHandler to set
     */
    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }
    
    /**
     * Archive walker for TAR archives.
     * @author rflitcroft
     *
     */
    private final class TarArchiveWalker extends ArchiveFileWalker<TarArchiveEntry> {
        
        private long parentId;
        private long originatorNodeId;
        private URI parentName;
        private ArchiveInputStream in;
        private Map<String, Long> directories = new HashMap<String, Long>();
        
        TarArchiveWalker(ArchiveInputStream in, long parentId, long originatorNodeId, URI parentName) {
            this.in = in;
            this.parentId = parentId;
            this.parentName = parentName;
            this.originatorNodeId = originatorNodeId;
        }
        
        @Override
        protected void handleEntry(TarArchiveEntry entry) throws IOException {
            // strip trailing slash
            String entryName = StringUtils.stripEnd(entry.getName(), "\\/");
            final String prefixPath = FilenameUtils.getPath(entryName);
            // fish the correlation ID out of the directories encountered
            
            String name = FilenameUtils.getName(entryName);
            Long correlationId = directories.get(prefixPath);
            if (correlationId == null) {
                correlationId = parentId;
                name = entryName;
            }
            
            if (entry.isDirectory()) {
                IdentificationResultImpl result = new IdentificationResultImpl();
                RequestMetaData metaData = new RequestMetaData(null, null, entryName);
                
                RequestIdentifier identifier = new RequestIdentifier(
                        ArchiveFileUtils.toTarUri(parentName, entry.getName()));
                identifier.setParentId(correlationId);
                identifier.setAncestorId(originatorNodeId);
                
                result.setRequestMetaData(metaData);
                result.setIdentifier(identifier);
                long dirId = resultHandler.handleDirectory(result, correlationId, false);
                directories.put(entry.getName(), dirId);
            } else {
                submit(entry, name, parentName, in, correlationId, originatorNodeId);
            }
        }

        
    }

}
