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
 * @author rflitcroft
 *
 */
public class GZipIdentificationRequest implements IdentificationRequest {

    private static final String ERROR_MESSAGE = "No byte at position [%s]";

    private static final int BUFFER_CACHE_CAPACITY = 10;
    private static final int CAPACITY = 50 * 1024; // 50 kB

    private String extension;
    private String fileName;
    private long size;

    private CachedBinary cachedBinary;
    private File tempFile;

    private int cacheCapacity;
    private int blockSize;
    private File tempDir;
    
    private RequestMetaData requestMetaData;
    private RequestIdentifier identifier;
    
    /**
     * Constructs a new GZip file resource.
     * @param metaData the name of the tar entry
     * @param identifier request identification object
     * @param tempDir the location to write temp files.
     */
    public GZipIdentificationRequest(RequestMetaData metaData, RequestIdentifier identifier, File tempDir) {
        this(metaData, identifier, CAPACITY, BUFFER_CACHE_CAPACITY, tempDir);
    }

    /**
     * Constructs a new GZip file resource.
     * @param metaData the name of the tar entry
     * @param identifier request identification object
     * @param cacheCapacity the buffer cache capacity
     * @param blockSize the cache block size
     * @param tempDir the location to write temp files.
     */
    GZipIdentificationRequest(RequestMetaData metaData, RequestIdentifier identifier,
            int cacheCapacity, int blockSize, File tempDir) {
        this.identifier = identifier;
        
        String path = identifier.getUri().getSchemeSpecificPart();
        extension = FilenameUtils.getExtension(path);
        fileName = FilenameUtils.getName(path);
        this.cacheCapacity = cacheCapacity;
        this.blockSize = blockSize;
        this.tempDir = tempDir;
        this.requestMetaData = metaData;

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void open(InputStream in) throws IOException {
        ReadableByteChannel channel = Channels.newChannel(in);
        ByteBuffer buffer = ByteBuffer.allocateDirect(blockSize);
        
        int bytesRead = 0;
        do {
            bytesRead = channel.read(buffer);
        } while (bytesRead >= 0 && buffer.hasRemaining());
        
        cachedBinary = new CachedBinary(cacheCapacity, blockSize, buffer);
        if (buffer.limit() == buffer.capacity()) {
            tempFile = ArchiveFileUtils.writeEntryToTemp(tempDir, buffer, channel);
            RandomAccessFile raf = new RandomAccessFile(tempFile, "r");
            cachedBinary.setRaf(raf);
            size = raf.length();
            
        } else {
            size = buffer.limit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (cachedBinary != null) {
            cachedBinary.close();
        }
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
        return cachedBinary.getByte(position);
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
     * @return the internal binary cache;
     */
    CachedBinary getCache() {
        return cachedBinary;
    }
    
    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public InputStream getSourceInputStream() throws IOException {
        return cachedBinary.getSourceInputStream();
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
