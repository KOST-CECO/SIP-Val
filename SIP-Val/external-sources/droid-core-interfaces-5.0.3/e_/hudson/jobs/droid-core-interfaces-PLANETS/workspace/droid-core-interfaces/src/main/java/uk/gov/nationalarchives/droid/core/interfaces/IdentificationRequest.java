/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces;

import java.io.IOException;
import java.io.InputStream;

import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

/**
 * Encapsulates an identification request.
 * @author rflitcroft
 *
 */
public interface IdentificationRequest {

    /**
     * Returns an array of bytes starting at 'from' of the length specified.
     * @param position the position of the byte .
     * @return an array of bytes.
     */
    byte getByte(long position);
    
    /**
     * Returns the file name. 
     * @return the file name
     */
    String getFileName();
    
    /**
     * @return the size of the resource in bytes.
     */
    long size();

    /**
     * @return The file extension.
     */
    String getExtension();
    
    /**
     * Releases resources for this resource.
     * @throws IOException if the resource could not be closed
     */
    void close() throws IOException;

    /**
     * Gets the binary source of this request. THis is useful when we want 
     * to futher process the binary, eg. treat the source as an archive and submit
     * its contents.
     * 
     * @return an InputStream which will read the binary data which formed the source
     * of this request.  
     * @return
     * @throws IOException  if there was an error reading from the binary source
     */
    InputStream getSourceInputStream() throws IOException;
    
    /**
     * Opens the request's input stream for reading.
     * @param in th input stream to use.
     * @throws IOException if the input stream could not be opened
     */
    void open(InputStream in) throws IOException;

    /**
     * @return the meta data.
     */
    RequestMetaData getRequestMetaData();

    /**
     * Returns an object which is used to identify the request's source and its place in a node hierarchy.
     * @return the identifier
     */
    RequestIdentifier getIdentifier();
}
