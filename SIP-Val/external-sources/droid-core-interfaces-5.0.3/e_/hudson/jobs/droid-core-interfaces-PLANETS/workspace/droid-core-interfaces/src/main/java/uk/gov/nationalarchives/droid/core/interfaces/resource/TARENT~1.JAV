/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FilenameUtils;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.archive.ArchiveFileUtils;

/**
 * Encapsulates a request for a GZIP entry.
 * @author rflitcroft
 *
 */
public class TarEntryIdentificationRequest implements IdentificationRequest {

    private static final String ERROR_MESSAGE = "No byte at position [%s]";

    private static final int BUFFER_CACHE_CAPACITY = 10;

    private static final int CAPACITY = 50 * 1024; // 50 kB
    
    private Long size;
    private String extension;
    
    private CachedBinary binaryCache;

    private String fileName;
    private File tempFile;

    private int lruCapacity;
    private int bufferCapacity;
    private File tempDir;
    
    private RequestMetaData requestMetaData;
    private RequestIdentifier identifier;
    
    /**
     * Constructs a new Zip file resource.
     * @param metaData the request meta data
     * @param identifier the request identifier
     * @param tempDir the location to write temp files.
     */
    public TarEntryIdentificationRequest(RequestMetaData metaData, RequestIdentifier identifier, File tempDir) {
        this(metaData, identifier, BUFFER_CACHE_CAPACITY, CAPACITY, tempDir);
    }
    
    /**
     * Constructs a new Zip file resource.
     * @param metaData the request meta data
     * @param identifier the request identifier
     * @param lruCapacity the buffer cache capacity
     * @param bufferCapacity the buffer capacity
     * @param tempDir the location to write temp files.
     */
    TarEntryIdentificationRequest(RequestMetaData metaData, RequestIdentifier identifier,
            int lruCapacity, int bufferCapacity, File tempDir) {
        this.identifier = identifier;
        size = metaData.getSize();
        
        fileName = metaData.getName();
        extension = FilenameUtils.getExtension(fileName);
        
        this.lruCapacity = lruCapacity;
        this.bufferCapacity = bufferCapacity;
        this.tempDir = tempDir;
        this.requestMetaData = metaData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void open(InputStream in) throws IOException {
        ReadableByteChannel channel = Channels.newChannel(in);
        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferCapacity);
        int bytesRead = 0;
        do {
            bytesRead = channel.read(buffer);
        } while (bytesRead >= 0 && buffer.hasRemaining());
        
        binaryCache = new CachedBinary(lruCapacity, bufferCapacity, buffer);
        if (buffer.limit() == buffer.capacity()) {
            tempFile = ArchiveFileUtils.writeEntryToTemp(tempDir, buffer, channel);
            RandomAccessFile raf = new RandomAccessFile(tempFile, "r");
            binaryCache.setRaf(raf);
            this.size = raf.length();
        } else {
            size = (long) buffer.limit();
        }
    }
    
    /**
     * Releases resources for this resource.
     * @throws IOException if the resource could not be closed
     */
    @Override
    public void close() throws IOException {
        binaryCache.close();
        if (tempFile != null) {
            tempFile.delete();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(long position) {
        
        if (size <= position) {
            throw new IndexOutOfBoundsException(
                    String.format(ERROR_MESSAGE, position));
        }
        
        return binaryCache.getByte(position);
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtension() {
        return extension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return size;
    }

    /**
     * @return the raf
     */
    CachedBinary getCache() {
        return binaryCache;
    }
    
    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public InputStream getSourceInputStream() throws IOException {
        return binaryCache.getSourceInputStream();
    }
    
    /**
     * @return the tempFile
     */
    File getTempFile() {
        return tempFile;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RequestMetaData getRequestMetaData() {
        return requestMetaData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RequestIdentifier getIdentifier() {
        return identifier;
    }

}
