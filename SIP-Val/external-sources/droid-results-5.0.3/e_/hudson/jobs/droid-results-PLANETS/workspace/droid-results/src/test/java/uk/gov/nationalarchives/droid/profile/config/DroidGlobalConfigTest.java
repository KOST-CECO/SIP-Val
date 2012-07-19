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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.nationalarchives.droid.RuntimeConfig;

/**
 * @author rflitcroft
 *
 */
public class DroidGlobalConfigTest {

    private File droidProperties;

    @BeforeClass
    public static void clearSystemProperties() {
        System.clearProperty(RuntimeConfig.DROID_WORK);
    }
    
    @Before
    public void setup() {
        
        assertNull(System.getenv().get(RuntimeConfig.DROID_WORK));
        assertNull(System.getProperty(RuntimeConfig.DROID_WORK));
        RuntimeConfig.configureRuntimeEnvironment();
        
        String userHome = System.getProperty("user.home");
        File droidHome = new File(userHome, ".droid");
        droidProperties = new File(droidHome, DroidGlobalConfig.DROID_PROPERTIES);
        droidProperties.delete();
    }
    
    @After
    public void tearDown() {
        System.clearProperty(RuntimeConfig.DROID_WORK);
    }
    
    @Test
    public void testDefaultHome() throws IOException {
        DroidGlobalConfig config = new DroidGlobalConfig();
        File expectedHome = new File(System.getProperty("user.home"), ".droid");
        assertEquals(expectedHome, config.getDroidWorkDir());
    }
    
    @Test
    public void testSystemPropertyHome() throws IOException {
        System.setProperty(RuntimeConfig.DROID_WORK, "custom_home");
        try {
            DroidGlobalConfig config = new DroidGlobalConfig();
            File expectedHome = new File("custom_home");
            assertEquals(expectedHome, config.getDroidWorkDir());
        } finally {
            System.clearProperty(RuntimeConfig.DROID_WORK);
        }
    }

    @Test
    public void testInitialisationWritesDefaultConfigInDefaultLocation() throws Exception {
        
        DroidGlobalConfig config = new DroidGlobalConfig();
        config.init();
        
        PropertiesConfiguration props = new PropertiesConfiguration(droidProperties);
        assertEquals(10, props.getInt(DroidGlobalProperty.DEFAULT_THROTTLE.getName()));
    }

    @Test
    public void testGetProfileProperties() throws Exception {
        
        DroidGlobalConfig config = new DroidGlobalConfig();
        config.init();
        
        Properties profileProperties = config.getProfileProperties();
        assertEquals("10", profileProperties.getProperty("defaultThrottle"));
    }
    
    @Test
    public void testUpdate() throws Exception {
        DroidGlobalConfig config = new DroidGlobalConfig();
        config.init();
        
        Map<String, Object> changedProperties = new HashMap<String, Object>();
        changedProperties.put(DroidGlobalProperty.DEFAULT_THROTTLE.getName(), "123");

        config.update(changedProperties);
        
        assertEquals(123, config.getProperties().getInt(DroidGlobalProperty.DEFAULT_THROTTLE.getName()));
        
        PropertiesConfiguration props = new PropertiesConfiguration(droidProperties);
        assertEquals(123, props.getInt(DroidGlobalProperty.DEFAULT_THROTTLE.getName()));
    }
}
