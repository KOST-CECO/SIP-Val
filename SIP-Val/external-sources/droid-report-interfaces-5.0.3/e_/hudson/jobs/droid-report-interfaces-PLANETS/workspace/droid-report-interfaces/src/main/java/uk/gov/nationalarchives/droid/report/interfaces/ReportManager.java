/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.report.interfaces;

import java.util.List;
import java.util.Map;

import uk.gov.nationalarchives.droid.planet.xml.dao.PlanetsXMLData;
import uk.gov.nationalarchives.droid.profile.ProfileInstance;



/**
 * @author Alok Kumar Dash
 */
public interface ReportManager {
/**
 * Generates planet xml and writes to path provided.
 * @param profileId Profile Id.
 * @param nameAndPathOfTheFile Path and name of the file.
 */
    void generatePlanetsXML(String profileId,
            String nameAndPathOfTheFile);
    
    /**
     * Generates planet xml and writes to path provided.
     * @return Map<ProfileInstance, PlanetsXMLData> map of results.
     * @param profileIds Profile Ids.
     */
    Map<ProfileInstance, PlanetsXMLData>  getReportData(List<String> profileIds);
    
    
    /**
     * Sets Observer.
     * @param observer Observer for the profress bar.
     */
    
    void setObserver(PlanetXMLProgressObserver observer);
    

}
