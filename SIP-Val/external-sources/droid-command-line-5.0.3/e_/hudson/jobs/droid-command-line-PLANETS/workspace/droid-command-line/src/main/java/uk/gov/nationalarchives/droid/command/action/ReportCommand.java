/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.gov.nationalarchives.droid.profile.ProfileInstance;
import uk.gov.nationalarchives.droid.profile.ProfileManager;
import uk.gov.nationalarchives.droid.report.interfaces.ReportManager;
import uk.gov.nationalarchives.droid.results.handlers.ProgressObserver;

/**
 * @author Alok Kumar Dash
 * 
 */
public class ReportCommand implements DroidCommand {

    private String[] profiles;
    private ReportManager reportManager;
    private ProfileManager profileManager;
    private String destination;
    private String reportType;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws CommandExecutionException {
        List<String> profileIds = new ArrayList<String>();

        // load each profile
        for (String profileLocation : profiles) {
            ProfileInstance profile;
            try {
                profile = profileManager.open(new File(profileLocation),
                        new ProgressObserver() {
                            @Override
                            public void onProgress(Integer progress) {
                            }
                        });
                profileIds.add(profile.getUuid());
            } catch (IOException e) {
                throw new CommandExecutionException(e);
            }
        }

        // Run the report
        if ("planets".equalsIgnoreCase(reportType)) {
            reportManager.generatePlanetsXML(profileIds.get(0), destination);
        } else {
            throw new CommandExecutionException(reportType + " report is not a valid report type.");
        }

        profileManager.closeProfile(profileIds.get(0));

    }

    /**
     * @param profileList
     *            the list of profiles to export.
     */
    public void setProfiles(String[] profileList) {
        this.profiles = profileList;
    }

    /**
     * @return the profiles
     */
    String[] getProfiles() {
        return profiles;
    }

    /**
     * @param profileManager
     *            the profileManager to set
     */
    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    /**
     * @param destination
     *            the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the destination
     */
    String getDestination() {
        return destination;
    }

    /**
     * @param reportManager
     *            the reportManager to set
     */
    public void setReportManager(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    /**
     * @param reportType
     *            the reportType to set
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

}
