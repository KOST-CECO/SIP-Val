/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.export.interfaces;

import java.util.List;
import java.util.concurrent.Future;

import uk.gov.nationalarchives.droid.core.interfaces.filter.Filter;


/**
 * @author rflitcroft
 *
 */
public interface ExportManager {

    /**
     * @param profileIds the list of profiles to export.
     * @param destination the destination filename
     * @param filter optional filter
     * @return future for cancelling the task. 
     */
    Future<?> exportProfiles(List<String> profileIds, String destination, Filter filter);

}
