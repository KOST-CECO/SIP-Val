/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Utilities.
 * @author rflitcroft
 *
 */
public final class ArchiveFileUtils {

    private static final String SSP_DELIMITER = ":/";
    private static final String ARCHIVE_DELIMITER = "!/";
    private static final int WRITE_BUFFER_CAPACITY = 8192;

    private ArchiveFileUtils() { };
    
    /**
     * Builds a URI for a zip file entry.
     * @param parent the parent zip file.
     * @param zipEntry the zip entry
     * @return the URI
     */
    public static URI toZipUri(URI parent, String zipEntry) {
        
        try {
            return new URI("zip:" + parent + ARCHIVE_DELIMITER 
                    + new URI(null, FilenameUtils.separatorsToUnix(zipEntry), null));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * Builds a URI for a tar file entry.
     * @param parent the parent tar file.
     * @param tarEntry the tar entry
     * @return the URI
     */
    public static URI toTarUri(URI parent, String tarEntry) {
        try {
            return new URI("tar:" + parent + ARCHIVE_DELIMITER 
                  + new URI(null, tarEntry, null));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Write contents of <code>buffer</code> to a temporary file, followed by the remaining bytes
     * in <code>channel</code>.
     * <p/>
     * <p>The bytes are read from <code>buffer</code> from the current position to its limit.
     *
     * @param buffer  contains the contents of the channel read so far
     * @param channel the rest of the channel
     * @param tempDir the directory in which to create the temp file
     * @return <code>File</code> object for the temporary file.
     * @throws java.io.IOException if there is a problem writing to the file
     */
    public static File writeEntryToTemp(File tempDir, ByteBuffer buffer, 
            ReadableByteChannel channel) throws IOException {
        final File tempFile = File.createTempFile("droid-archive", null, tempDir);
        final FileChannel out = (new FileOutputStream(tempFile)).getChannel();
        
        try {
            out.write(buffer);
    
            final ByteBuffer buf = ByteBuffer.allocate(WRITE_BUFFER_CAPACITY);
            buf.clear();
            while (channel.read(buf) >= 0 || buf.position() != 0) {
                buf.flip();
                out.write(buf);
                buf.compact();    // In case of partial write
            }
            return tempFile;
        } finally {
            out.close();
        }
        
    }

    /**
     * @param parent the container file
     * @return a GZIP URI
     */
    public static URI toGZipUri(URI parent) {
        try {
            return new URI("gz:" + parent + ARCHIVE_DELIMITER + new URI(null, 
                    GzipUtils.getUncompressedFilename(FilenameUtils.getName(parent.getSchemeSpecificPart())), null));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param requestUri a uri
     * @return the URI needed to replay this uri.
     */
    public static URI toReplayUri(URI requestUri) {
        String originSsp = StringUtils.substringBetween(requestUri.toString(), SSP_DELIMITER, "!");
        String scheme = StringUtils.substringBefore(requestUri.toString(), SSP_DELIMITER);
        if (originSsp != null) {
            return URI.create(StringUtils.substringAfterLast(scheme, ":") + SSP_DELIMITER + originSsp);
        }
        
        return requestUri;
    }

}
