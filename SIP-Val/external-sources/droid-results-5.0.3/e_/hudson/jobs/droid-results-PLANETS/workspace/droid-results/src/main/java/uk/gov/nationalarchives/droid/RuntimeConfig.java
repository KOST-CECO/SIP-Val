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

/**
 * Global runtime configuration utility.
 * @author rflitcroft
 *
 */
public final class RuntimeConfig {

    /**
     * The droid work system / environment property name.
     */
    public static final String DROID_WORK = "droidWorkDir";

    private RuntimeConfig() { }
    
    /**
     * Sets the "droidWorkDir" system property.
     */
    public static void configureRuntimeEnvironment() {
        
        // Configure the droid working area
        
        File workDir;
        String droidWorkPath = System.getProperty(DROID_WORK);
        if (droidWorkPath != null) {
            workDir = new File(droidWorkPath);
        } else {
            droidWorkPath = System.getenv(DROID_WORK);
            if (droidWorkPath != null) {
                workDir = new File(droidWorkPath);
            } else {
                workDir = new File(System.getProperty("user.home"), ".droid");
                droidWorkPath = workDir.getPath();
            }
        }
        
        workDir.mkdirs();
        
        System.setProperty(DROID_WORK, workDir.getPath());
        
        File logFile = new File(workDir, "logs/droid.log");
        System.setProperty("logFile", logFile.getPath());
        
    }
}
