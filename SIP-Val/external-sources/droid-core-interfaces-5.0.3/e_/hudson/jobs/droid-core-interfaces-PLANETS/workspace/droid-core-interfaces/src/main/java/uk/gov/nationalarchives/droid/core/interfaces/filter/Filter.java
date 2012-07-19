/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.filter;

import java.util.List;


/**
 * @author rflitcroft
 *
 */
public interface Filter {

    /**
     * Getter method for filter criteria list.
     * @return List of filter criteria.
     */
    List<FilterCriterion> getCriteria();

    /**
     * @return true if the filter is enabled; false otherwise
     */
    boolean isEnabled();

    /**
     * @return true if ALL criteria must be satisfied, false if ANY of the criteria must be satisfied
     */
    boolean isNarrowed();

    /**
     * checks if filter is empty or filter criteria list exists. 
     * @return boolean 
     */
    boolean hasCriteria();

}
