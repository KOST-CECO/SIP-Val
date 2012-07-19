/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.report;


import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;



import uk.gov.nationalarchives.droid.planet.xml.dao.PlanetsXMLData;
import uk.gov.nationalarchives.droid.profile.ProfileInstance;
import uk.gov.nationalarchives.droid.report.interfaces.ReportManager;

/**
 * @author Alok Kumar Dash
 * 
 */
public class ReportAction extends SwingWorker {


    private List<String> profileIds;
    private ReportManager reportManager;

    private Map<ProfileInstance, PlanetsXMLData> reportData;

    /**
     * Default constructor.
     */

    public ReportAction() {

    }

    /**
     * @param profileIds
     *            ProfileIds
     */
    public ReportAction(List<String> profileIds) {
        this.profileIds = profileIds;
    }


    @Override
    protected void done() {

        // At first iterate through this data.
        // Build JRDataSource and pass it to report viewer.
        // Close the reporting dialog.
        // Once this is done it actually fire the viewer.
    }

    /**
     * @param reportManager
     *            the reportManager to set
     */
    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    /**
     * @return Void void.
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() {
        reportData = reportManager.getReportData(profileIds);
        firePropertyChange("STATE", "RUNNING", "DONE");
        return null;
    }

    /**
     * @param profileIds
     *            the profileIds to set
     */
    public void setProfileIds(List<String> profileIds) {
        this.profileIds = profileIds;
    }
}
