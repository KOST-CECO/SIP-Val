/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.collections.map.LazyMap;

/**
 * Wraps a binary data source to allow efficient Random Access reading.
 * @author rflitcroft
 *
 */
class CachedBinary {

    private static final String ERROR_MESSAGE = "No byte at position [%s]";

    private Map<Long, ByteBuffer> cache;
    private RandomAccessFile raf;
    private int bufferCapacity;

    /**
     * Creates a new Cached Binary.
     * @param blocks the maximum number of blocks to be held
     * @param blockCapacity the capacity of each block
     * @param blockZero the first block
     */
    @SuppressWarnings("unchecked")
    public CachedBinary(int blocks, int blockCapacity, ByteBuffer blockZero) {
        cache = LazyMap.decorate(new LRUMap(blocks), new CacheTransformer());
        bufferCapacity = blockCapacity;
        blockZero.flip();
        cache.put(0L, blockZero);
    }
    
    /**
     * Sets the optional Random Access File for the whoe binary.
     * @param file the binary data.
     */
    void setRaf(RandomAccessFile file) {
        raf = file;
    }
    
    /**
     * Transformer for building a buffer.
     * @author rflitcroft
     *
     */
    private final class CacheTransformer implements Transformer {
        @Override
        public Object transform(Object input) {
            Long block = (Long) input;
            try {
                ByteBuffer buffer = ByteBuffer.allocateDirect(bufferCapacity);
                // determine the block
                raf.seek(block);
                raf.getChannel().read(buffer);
                
                // If the buffer is not full, don't let anyone read past here.
                buffer.flip();
                return buffer;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
    
    /**
     * Gets the byte at the given position.
     * @param position the position, p
     * @return the byte at position p
     */
    byte getByte(long position) {
        Long block = positionToBlock(position);
        ByteBuffer buffer = cache.get(block);
        
        try {
            return buffer.get((int) (position - block));
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(
                    String.format(ERROR_MESSAGE, position));
        }
        
    }
    
    private Long positionToBlock(long position) {
        return (position / bufferCapacity) * bufferCapacity;
    }

    /**
     * Closes the internal Random Access File.
     * @throws IOException if the file could not be closed.
     */
    void close() throws IOException {
        if (raf != null) {
            raf.close();
        }
    }

    /**
     * Gets the internal buffer cache.
     * @return the internal buffer cache
     */
    Map<Long, ByteBuffer> getBuffers() {
        return cache;
    }

    /**
     * Gets the intyernal randon access file.
     * @return the internal random access file.
     */
    RandomAccessFile getRaf() {
        return raf;
    }

    /**
     * @return the source inpuit stream
     * @throws IOException if there was an exception reading the source
     */
    public InputStream getSourceInputStream() throws IOException {
        InputStream in;
        
        if (raf == null) {
            ByteBuffer blockZero = cache.get(0L);
            byte[] bytes = new byte[blockZero.limit()];
            blockZero.get(bytes);
            in = new ByteArrayInputStream(bytes);
        } else {
            final FileChannel channel = raf.getChannel();
            channel.position(0);
            in = Channels.newInputStream(channel);
        }
        
        return in;
    }

}
