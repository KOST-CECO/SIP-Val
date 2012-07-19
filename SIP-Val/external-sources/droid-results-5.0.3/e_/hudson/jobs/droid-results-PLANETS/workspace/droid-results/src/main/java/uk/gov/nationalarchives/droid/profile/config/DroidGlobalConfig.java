/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;

import static uk.gov.nationalarchives.droid.RuntimeConfig.DROID_WORK;

/**
 * @author rflitcroft
 * 
 */
public class DroidGlobalConfig {

    /** The name of the DROID properties file. */
    public static final String DROID_PROPERTIES = "droid.properties";

    private static final String DROID_SIGNATURE_FILE_V32 = "DROID_SignatureFile_V32.xml";

    private File droidWorkDir;
    private File signatureFilesDir;
    private PropertiesConfiguration props;

    private File profilesDir;
    private File tempDir;

    /**
     * Default Constructor. Initialises the droid home directory.
     * @throws IOException if there was an error writing the signature file.
     */
    public DroidGlobalConfig() throws IOException {
        String droidHomePath = System.getProperty(DROID_WORK);

        droidWorkDir = new File(droidHomePath);
        droidWorkDir.mkdirs();
        
        signatureFilesDir = new File(droidWorkDir, "signature_files");
        if (signatureFilesDir.mkdir()) {
            InputStream in = getClass().getClassLoader().getResourceAsStream(DROID_SIGNATURE_FILE_V32);
            File sigFile = new File(signatureFilesDir, DROID_SIGNATURE_FILE_V32);
            sigFile.createNewFile();
            OutputStream out = new FileOutputStream(sigFile);
            IOUtils.copy(in, out);
        }

        profilesDir = new File(droidWorkDir, "profiles");
        profilesDir.mkdir();
        
        tempDir = new File(droidWorkDir, "tmp");
        tempDir.mkdir();
    }

    /**
     * Initialises the droid config bean.
     * 
     * @throws ConfigurationException
     *             if the config could not be intialised
     */
    public void init() throws ConfigurationException {

        File droidProperties = new File(droidWorkDir, DROID_PROPERTIES);
        props = new PropertiesConfiguration(droidProperties);

        URL defaultPropsUrl = getClass().getClassLoader().getResource(
                "default_droid.properties");
        PropertiesConfiguration defaultProps = new PropertiesConfiguration(
                defaultPropsUrl);

        boolean saveProperties = false;
        for (Iterator<String> it = defaultProps.getKeys(); it.hasNext();) {
            String key = it.next();
            if (!props.containsKey(key)) {
                props.addProperty(key, defaultProps.getProperty(key));
                saveProperties = true;
            }
        }

        if (saveProperties) {
            props.save();
        }
    }

    /**
     * @return the droidHomeDir
     */
    public File getDroidWorkDir() {
        return droidWorkDir;
    }

    /**
     * @return all profile-realted properties
     */
    public Properties getProfileProperties() {
        Properties profileProperties = new Properties();
        
        final Configuration profilePropsConfig = props.subset("profile");
        for (Iterator<String> it = profilePropsConfig.getKeys(); it.hasNext();) {
            String key = it.next();
            profileProperties.setProperty(key, profilePropsConfig.getString(key));
        }
        
        return profileProperties;
    }

    /**
     * @return the property configuration;
     */
    public PropertiesConfiguration getProperties() {
        return props;
    }

    /**
     * Updates the config with the properties given and persists. 
     * @param properties the changed properties
     * @throws ConfigurationException if the config could not be saved.
     */
    public void update(Map<String, Object> properties) throws ConfigurationException {
        for (Entry<String, Object> entry : properties.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue());
        }
        
        props.save();
    }

    /**
     * @return all settings in a map
     */
    public Map<String, Object> getPropertiesMap() {
        final Map<String, Object> allSettings = new HashMap<String, Object>();
        for (Iterator<String> it = props.getKeys(); it.hasNext();) {
            String key = it.next();
            DroidGlobalProperty property = DroidGlobalProperty.forName(key);
            if (property != null) {
                allSettings.put(key, property.getType().getTypeSafeValue(props, key));
            }
        }
        
        return allSettings;
    }
    
    /**
     * 
     * @return the directory where droid signature files reside.
     */
    public File getSignatureFileDir() {
        return signatureFilesDir;
    }
    
    /**
     * @return the profilesDir
     */
    public File getProfilesDir() {
        return profilesDir;
    }

    /**
     * @return the directory for droid temporary files
     */
    public File getTempDir() {
        return tempDir;
    }
    
}
