/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.planet.xml.dao.PlanetsXMLData;
import uk.gov.nationalarchives.droid.profile.AbstractProfileResource;
import uk.gov.nationalarchives.droid.profile.ProfileContextLocator;
import uk.gov.nationalarchives.droid.profile.ProfileInstance;
import uk.gov.nationalarchives.droid.profile.ProfileInstanceManager;
import uk.gov.nationalarchives.droid.report.interfaces.PlanetXMLProgressObserver;
import uk.gov.nationalarchives.droid.report.interfaces.ReportManager;
import uk.gov.nationalarchives.droid.report.planets.xml.PlanetsXMLGenerator;

//import uk.gov.nationalarchives.droid.report.planets.xml.PlanetsXMLGenerator;

/**
 * @author Alok Kumar Dash
 */
public class ReportManagerImpl implements ReportManager {

    private final Log log = LogFactory.getLog(getClass());

    private ProfileContextLocator profileContextLocator;

    private PlanetXMLProgressObserver observer;

    /**
     * Default Constructor.
     */
    public ReportManagerImpl() {

    }

    
    
    /**
     * 
     * @param observer
     *            ProgressObserver.
     */
    public ReportManagerImpl(PlanetXMLProgressObserver observer) {
        this.observer = observer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generatePlanetsXML(String profileId, String nameAndPathOfTheFile) {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        if (!profileContextLocator.hasProfileContext(profileId)) {
            throw new RuntimeException("Profile  not available");
        }
        ProfileInstance profile = profileContextLocator
                .getProfileInstance(profileId);
        ProfileInstanceManager profileInstancemanager = profileContextLocator
                .openProfileInstanceManager(profile);

        PlanetsXMLData planetsData = profileInstancemanager.getPlanetsData();

        planetsData.getProfileStat().setProfileStartDate(
                profile.getProfileStartDate());
        planetsData.getProfileStat().setProfileEndDate(
                profile.getProfileEndDate());
        planetsData.getProfileStat().setProfileSaveDate(
                profile.getDateCreated());

        List<AbstractProfileResource> profileResources = profile
                .getProfileSpec().getResources();

        List<String> profileResourcesList = new ArrayList<String>();

        for (AbstractProfileResource resource : profileResources) {
            profileResourcesList.add(new File(resource.getUri()).getPath());

        }

        planetsData.setTopLevelItems(profileResourcesList);

        PlanetsXMLGenerator planetXMLGenerator = new PlanetsXMLGenerator(
                observer, nameAndPathOfTheFile, planetsData);
        planetXMLGenerator.generate();

        stopWatch.stop();
        log.info(String.format("Time for profile [%s]: %s ms", profileId,
                stopWatch.getTime()));
        stopWatch.reset();

    }

    /**
     * Generates planet xml and writes to path provided.
     * 
     * @param profileIds
     *            Profile Ids.
     * @return reportData.           
     */
    @Override
    public Map<ProfileInstance, PlanetsXMLData> getReportData(List<String> profileIds) {

        Map<ProfileInstance, PlanetsXMLData> reportData = 
            new HashMap<ProfileInstance, PlanetsXMLData>();

       

        for (String profileId : profileIds) {

            if (!profileContextLocator.hasProfileContext(profileId)) {
                throw new RuntimeException("Profile not available");
            }

            ProfileInstance profile = profileContextLocator
                    .getProfileInstance(profileId);

            ProfileInstanceManager profileInstancemanager = profileContextLocator
                    .openProfileInstanceManager(profile);

            PlanetsXMLData planetsData = profileInstancemanager
                    .getPlanetsData();
            
            reportData.put(profile, planetsData);

        }
        
        return reportData;        
        

    }

    /**
     * @param profileContextLocator
     *            the profileContextLocator to set
     */
    public void setProfileContextLocator(
            ProfileContextLocator profileContextLocator) {
        this.profileContextLocator = profileContextLocator;
    }

    /**
     * @param observer
     *            the observer to set
     */
    public void setObserver(PlanetXMLProgressObserver observer) {
        this.observer = observer;
    }

}








/**
 * import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class MyDataSource implements JRDataSource {
    public MyDataSource(javax.swing.JList myList) {
        {
           // the columns number of report is already khnown, 
           // furthermore you need to count rows number to create the data matrix
        }
        data = new Object[numero_righe][numero_colonne];
        
        {
           // insert here your code to read the JList myList and
           //  to fill the objects matrix 'data', whose column fields are so named:
           // field1, field2, filed3, field4
        }
       
    }
    
    // called in net.sf.jasperreports.engine.fill package from JRBaseFiller.next() method
    // to make certain further data exist
    public boolean next() throws JRException {
        index++;

        return (index < data.length);
    }

    // called in net.sf.jasperreports.engine.fill package from JRBaseFiller.next() method
    // to get the right field value by means of column name an current record index
    public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
       
        String fieldName = field.getName();
       
        if ("field1".equals(fieldName)) {
            value = data[index][0];
        }
        else if ("field1".equals(fieldName)) {
            value = data[index][1];
        }
        else if ("field1".equals(fieldName)) {
            value = data[index][2];
        }
        else if ("field1".equals(fieldName)) {
            value = data[index][3];
        }
               
        return value;
    }

    private Object[][] data;  // data source structure
    private int index = -1;  // current record index

} // end of class
*/
