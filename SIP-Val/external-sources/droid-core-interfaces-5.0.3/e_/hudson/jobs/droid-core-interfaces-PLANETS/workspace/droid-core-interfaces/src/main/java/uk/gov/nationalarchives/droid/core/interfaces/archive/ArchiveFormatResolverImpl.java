/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.archive;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves format PUIDS to an archive format type which can be handled.
 * @author rflitcroft
 *
 */
public class ArchiveFormatResolverImpl implements ArchiveFormatResolver {

    private Map<String, ArchiveFormat> puidsMap = new HashMap<String, ArchiveFormat>();

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveFormat forPuid(String puid) {
        return puidsMap.get(puid);
    }
    
    /**
     * Sets the format -> puid mapping.
     * 
     * @param puids the PUIDS to set
     */
    public void setPuids(Map<ArchiveFormat, String> puids) {
        for (Map.Entry<ArchiveFormat, String> entry : puids.entrySet()) {
            final String[] puidsIn = entry.getValue().split(",");
            for (String puid : puidsIn) {
                puidsMap.put(puid.trim(), entry.getKey());
            }
        }
    }
    
}
