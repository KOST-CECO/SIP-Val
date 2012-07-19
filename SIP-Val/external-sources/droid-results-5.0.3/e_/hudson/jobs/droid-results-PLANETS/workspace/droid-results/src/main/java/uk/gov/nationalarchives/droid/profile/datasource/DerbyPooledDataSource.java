/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile.datasource;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.RuntimeConfig;

/**
 * @author rflitcroft
 *
 */
public class DerbyPooledDataSource extends BasicDataSource {
 
    
    private static final long serialVersionUID = -8613139738021279720L;

    private final Log log = LogFactory.getLog(getClass());
    
    /**
     * Starts the database.
     * @throws SQLException if the database could not be booted.
     */
    public void init() throws SQLException {
        String droidWorkDir = System.getProperty(RuntimeConfig.DROID_WORK);
        System.setProperty("derby.stream.error.file", 
                new File(droidWorkDir, "logs/derby.log").getPath());
        log.info(String.format("Booting database [%s]", getUrl()));

        String createUrl = getUrl() + ";create=true";
        String driverClassName = getDriverClassName();
        try {
            Class.forName(driverClassName);
            DriverManager.getConnection(createUrl).close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid driver class name: " + driverClassName, e);
        }
    }
    
    /**
     * Shuts down the database.
     * @throws SQLException if the database could not be shutdown.
     * @throws Exception
     */
    public void shutdown() throws SQLException {
        
        log.info(String.format("Closing database [%s]", getUrl()));
        close();

        String createUrl = getUrl() + ";shutdown=true";
        
        try {
            DriverManager.getConnection(createUrl);
        } catch (SQLNonTransientConnectionException e) {
            log.info(e.getMessage());
            log.info(String.format("Database closed [%s]", getUrl()));
        }
    }
    
}
