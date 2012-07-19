/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.util.Properties;

/**
 * @author rflitcroft
 *
 */
public interface ProfileInstanceLocator {

    /**
     * Returns a profile instance manager which will manage the profile instance at the specified
     * location.
     * @param profile - the filesystem path where the profile instance is located.
     * @param properties - properties to be passed to the profile instance.
     * @param observer 
     * @return a profile instance manager
     */
    ProfileInstanceManager getProfileInstanceManager(ProfileInstance profile, Properties properties);

    /**
     * Closes the specified profile instrance an cleans up all associated resources.
     * @param location - the filesystem path where the profile instance is located.
     */
    void closeProfileInstance(String location);

    /**
     * Returns a profile instance manager which will manage the profile instance at the specified
     * location.
     * @param profileId the ID of the profile
     * @return a profile instance manager
     */
    ProfileInstanceManager getProfileInstanceManager(String profileId);

    /**
     * Shuts down the profile database.
     * @param profileId the ID of the profile.
     */
    void shutdownDatabase(String profileId);

    /**
     * Starts a profile database.
     * @param profileId the profile ID
     */
    void bootDatabase(String profileId);
}
