/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core;

import java.util.ArrayList;
import java.util.List;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.signature.droid4.FileFormatHit;
import uk.gov.nationalarchives.droid.core.signature.droid4.bytereader.ByteReader;

/**
 * Adapts an IdentificationRequest to the ByteReader interface.
 * @author rflitcroft
 *
 */
public class IdentificationRequestByteReaderAdapter implements ByteReader {
    
    private IdentificationRequest request;
    private long fileMarker;
    
    private List<FileFormatHit> hits = new ArrayList<FileFormatHit>();
    
    /**
     * 
     * @param request the request to wrap.
     */
    public IdentificationRequestByteReaderAdapter(IdentificationRequest request) {
        this.request = request;
    }

    /**
     * @param theHit the hit to add
     */
    @Override
    public void addHit(FileFormatHit theHit) {
        hits.add(theHit);
    }

    /* (non-Javadoc)
     * @see uk.gov.nationalarchives.droid.core.signature.droid4.bytereader.ByteReader#close()
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Not supported.
     * @return Not Supported
     */
    @Override
    public byte[] getbuffer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the byte oit the index specified.
     * @param fileIndex the file index
     * @return the byte at index fileIndex
     * 
     */
    @Override
    public byte getByte(long fileIndex) {
        return request.getByte(fileIndex);
    }

    /**
     * Not supported.
     * @return Not Supported
     */
    @Override
    public int getClassification() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the File name of the request
     */
    @Override
    public String getFileName() {
        return request.getFileName();
    }

    /**
     * @return Not Supported
     */
    @Override
    public String getFilePath() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param theIndex the index of the hit
     * @return the hit at the specified index
     */
    @Override
    public FileFormatHit getHit(int theIndex) {
        return hits.get(theIndex);
    }

    /**
     * @return Not supported
     */
    @Override
    public String getIdentificationWarning() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the nuimber of bytes available from this resource.
     */
    @Override
    public long getNumBytes() {
        return request.size();
    }

    /**
     * @return the number of hits
     */
    @Override
    public int getNumHits() {
        return hits.size();
    }

    /**
     * @return Not supported
     */
    @Override
    public boolean isClassified() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes a hit at thindex specified.
     * @param theIndex th index of th hit to remove.
     */
    @Override
    public void removeHit(int theIndex) {
        hits.remove(theIndex);
    }

    /**
     * @Override
     */
    @Override
    public void setErrorIdent() {
        
    }


    /**
     * Sets an identification warning.
     * @param theWarning the warning to set.
     */
    @Override
    public void setIdentificationWarning(String theWarning) {
        throw new UnsupportedOperationException();
        
    }

    /**
     * @Override
     */
    @Override
    public void setNoIdent() {
        
    }

    /**
     * @Override
     */
    @Override
    public void setPositiveIdent() {
        
    }

    /**
     * @Override
     */
    @Override
    public void setTentativeIdent() {
        
    }

    /**
     * @return the fileMarker
     */
    public long getFileMarker() {
        return fileMarker;
    }

    /**
     * @param fileMarker the fileMarker to set
     */
    public void setFileMarker(long fileMarker) {
        this.fileMarker = fileMarker;
    }
    
    
    
}
