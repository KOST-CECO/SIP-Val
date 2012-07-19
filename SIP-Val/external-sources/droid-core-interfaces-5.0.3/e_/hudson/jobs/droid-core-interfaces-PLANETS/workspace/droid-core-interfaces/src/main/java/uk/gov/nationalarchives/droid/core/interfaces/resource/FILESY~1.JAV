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
import java.nio.channels.FileChannel;

import org.apache.commons.io.FilenameUtils;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;

/**
 * Encapsualted the binary data for a file system identification request.
 * @author rflitcroft
 *
 */
public class FileSystemIdentificationRequest implements IdentificationRequest {

    private static final String ERROR_MESSAGE = "No byte at position [%s]";

    private static final int BUFFER_CACHE_CAPACITY = 10;
    private static final int CAPACITY = 50 * 1024; // 50 kB

    private String extension;
    private String fileName;
    private long size;

    private CachedBinary cachedBinary;
    private RequestIdentifier identifier;

    private int lruCapacity;
    private int bufferCapacity;
    
    private RequestMetaData requestMetaData;

    /**
     * Constructs a new identification request.
     * @param metaData the metaData about the binary.
     * @param lruCapacity the buffer cache capacity
     * @param identifier the request's identifier
     * @param bufferCapacity the buffer capacity
     */
    FileSystemIdentificationRequest(RequestMetaData metaData, RequestIdentifier identifier,
            int lruCapacity, int bufferCapacity) {
        this.identifier = identifier;
        
        requestMetaData = metaData;
        size = metaData.getSize();
        fileName = metaData.getName();
        extension = FilenameUtils.getExtension(fileName);
        this.lruCapacity = lruCapacity;
        this.bufferCapacity = bufferCapacity;

    }
    
    /**
     * Constructs a new identification request.
     * @param metaData the metaData about the binary.
     * @param identifier the request's identifier
     */
    public FileSystemIdentificationRequest(RequestMetaData metaData, RequestIdentifier identifier) {
        this(metaData, identifier, BUFFER_CACHE_CAPACITY, CAPACITY);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void open(InputStream in) throws IOException {
        FileChannel channel = (FileChannel) Channels.newChannel(in);
        
        ByteBuffer blockZero = ByteBuffer.allocate(bufferCapacity);
        
        int bytesRead = 0;
        do {
            bytesRead = channel.read(blockZero);
        } while (bytesRead >= 0 && blockZero.hasRemaining());
        
        cachedBinary = new CachedBinary(lruCapacity, bufferCapacity, blockZero);
        if (blockZero.limit() == blockZero.capacity()) {
            cachedBinary.setRaf(new RandomAccessFile(new File(identifier.getUri()), "r"));
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
    
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public Date getLastModified() {
//        Date lastModified = new Date();
//        lastModified.setTime(time);
//        return lastModified;
//    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        cachedBinary.close();
    }

    /**
     * @return the internal binary cache
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
     * {@inheritDoc}
     */
    @Override
    public RequestMetaData getRequestMetaData() {
        return requestMetaData;
    }

    /**
     * @return the identifier
     */
    public RequestIdentifier getIdentifier() {
        return identifier;
    }

}
