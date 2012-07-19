/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author rflitcroft
 *
 */
public class RuntimeConfigTest {

    @AfterClass
    public static void tearDown() {
        System.clearProperty(RuntimeConfig.DROID_WORK);
    }
    
    @Before
    public void setup() {
        System.clearProperty(RuntimeConfig.DROID_WORK);
    }
    
    @Test
    public void testConfigureRuntimeEnvironmentUsingSystemProperty() {
        
        assertNull(System.getProperty(RuntimeConfig.DROID_WORK));
        System.setProperty(RuntimeConfig.DROID_WORK, "/tmp/droid");
        RuntimeConfig.configureRuntimeEnvironment();
        
        assertEquals(new File("/tmp/droid").getPath(), System.getProperty(RuntimeConfig.DROID_WORK));
        assertEquals(new File("/tmp/droid/logs/droid.log").getPath(), System.getProperty("logFile"));

    }

    @Test
    public void testConfigureDefaultRuntimeEnvironment() {
        
        assertNull(System.getProperty(RuntimeConfig.DROID_WORK));
        RuntimeConfig.configureRuntimeEnvironment();
        
        File userHome = new File(System.getProperty("user.home"));
        
        assertEquals(new File(userHome, ".droid").getPath(), System.getProperty(RuntimeConfig.DROID_WORK));
        assertEquals(new File(userHome, ".droid/logs/droid.log").getPath(), System.getProperty("logFile"));
    }
}
