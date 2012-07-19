/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.resource;


/**
 * @author rflitcroft
 *
 */
public class RequestMetaData {

    private Long size;
    private Long time;
    private String name;

    /**
     * @param size - the size in bytes of the request data
     * @param time - the time associated with data (e.g. last modified)
     * @param entryName the name of the request data
     */
    public RequestMetaData(Long size, Long time, String entryName) {
        this.size = size;
        this.time = time;
        this.name = entryName;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @return the time
     */
    public Long getTime() {
        return time;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
