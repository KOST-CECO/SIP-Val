/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

import uk.gov.nationalarchives.droid.profile.config.DroidGlobalConfig;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;
import uk.gov.nationalarchives.droid.signature.SignatureFileException;


/**
 * @author rflitcroft
 * Maintains profile contexts.
 */
public class ProfileContextLocator {

    private static final String HIBERNATE_GENERATE_DDL = "hibernate.generateDdl";
    private static final String DATABASE_URL = "datasource.url";
    private static final String HIBERNATE_CREATE = "hibernate.hbm2ddl.auto";
    
    private DroidGlobalConfig globalConfig;

    @SuppressWarnings("unchecked")
    private Map<String, ProfileInstance> profileInstances = 
        LazyMap.decorate(new HashMap<String, ProfileInstance>(), new ProfileTransformer());
    
    private ProfileInstanceLocator profileInstanceLocator;
    
    /**
     * Transformer for creayting profile instance objects.
     * @author rflitcroft
     */
    private final class ProfileTransformer implements Transformer {
        @Override
        public Object transform(Object id) {
            ProfileInstance profileInstance = new ProfileInstance(ProfileState.INITIALISING);
            profileInstance.setUuid((String) id);
            profileInstance.setThrottle(globalConfig.getProperties()
                    .getInt(DroidGlobalProperty.DEFAULT_THROTTLE.getName()));
            return profileInstance;
        }
    }
    
    /**
     * Lazily instantiates (if necessary) and returns the profile instance with the name given.
     * @param id the id of the profile instance
     * @return the profile instance with the name given
     */
    public ProfileInstance getProfileInstance(String id) {
        return profileInstances.get(id);
    }
    
    /**
     * Adds a profile context.
     * @param profileInstance the profile instance to add
     */
    public void addProfileContext(ProfileInstance profileInstance) {
        profileInstances.put(profileInstance.getUuid(), profileInstance);
    }
    
    /**
     * Destroys a profile context.
     * @param id the id of the contex to destroy
     */
    public void removeProfileContext(String id) {
        profileInstances.remove(id);
        profileInstanceLocator.closeProfileInstance(id);
        
    }
    
    /**
     * Shuts down the database for the specified profile, releasing all connections
     * and resources.
     * @param profileId the id of a profile.
     */
    public void shutdownDatabase(String profileId) {
        profileInstanceLocator.shutdownDatabase(profileId);
    }
    
    /**
     * Opens a profile instance manager for a pre-existing profile context.
     * @param profile the profile to obtain a profile manager for.
     * @return a profile instance manager for a pre-existing profile context
     */
    public ProfileInstanceManager openProfileInstanceManager(ProfileInstance profile) {
        
        File profileHome = new File(globalConfig.getProfilesDir(), profile.getUuid());
        File databasePath = new File(profileHome, "/db");
        File signatureFile = new File(profileHome, profile.getSignatureFileName());
        File submissionQueueFile = new File(profileHome, "submissionQueue.xml");

        // Some global properties are needed to initialise the profile context.
        Properties props = new Properties();
        props.setProperty("defaultThrottle", String.valueOf(profile.getThrottle()));
        props.setProperty("signatureFilePath", signatureFile.getPath());
        props.setProperty("submissionQueueFile", submissionQueueFile.getPath());
        props.setProperty("tempDirLocation", globalConfig.getTempDir().getPath());
        props.setProperty("profileHome", profileHome.getPath());
        props.setProperty("processArchives", String.valueOf(globalConfig.getProperties().getBoolean(
                DroidGlobalProperty.PROCESS_ARCHIVES.getName())));
        
        final boolean newDatabase = !databasePath.exists();
        
        props.setProperty(DATABASE_URL, String.format("jdbc:derby:%s", databasePath.getPath()));
        if (newDatabase) {
            props.setProperty(HIBERNATE_GENERATE_DDL, "true");
            props.setProperty(HIBERNATE_CREATE, "create");
        } else {
            props.setProperty(HIBERNATE_CREATE, "none");
            props.setProperty(HIBERNATE_GENERATE_DDL, "false");
        }
        
        ProfileInstanceManager profileManager = profileInstanceLocator.getProfileInstanceManager(profile, props);
        
        if (newDatabase) {
            try {
                profileManager.initProfile(signatureFile.toURI());
            } catch (SignatureFileException e) {
                throw new RuntimeException("Error reading signature file", e);
            }
        }
        
        return profileManager;
    }

    /**
     * @param profileInstanceLocator the profileInstanceLocator to set
     */
    public void setProfileInstanceLocator(
            ProfileInstanceLocator profileInstanceLocator) {
        this.profileInstanceLocator = profileInstanceLocator;
    }

    /**
     * @param profileName the profile ID
     * @return true if the context has this profile; false otherwise
     */
    public boolean hasProfileContext(String profileName) {
        return profileInstances.containsKey(profileName);
    }

    /**
     * Boots the database for the specified profile.
     * @param profileId the id of a profile.
     */
    public void bootDatabase(String profileId) {
        profileInstanceLocator.bootDatabase(profileId);
        
    }
    
    /**
     * @param globalConfig the globalConfig to set
     */
    public void setGlobalConfig(DroidGlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }
    
}
