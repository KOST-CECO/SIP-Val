package ch.bedag.a6z.sipvalidator.service.impl;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.SystemException;
import ch.bedag.a6z.sipvalidator.logging.Logger;
import ch.bedag.a6z.sipvalidator.service.ConfigurationService;
import ch.bedag.a6z.sipvalidator.service.JhoveService;
import ch.bedag.a6z.sipvalidator.util.StreamGobbler;
import ch.bedag.a6z.sipvalidator.util.Util;
/**
 * Dieser Service stellt die Schnittstelle zur JHove Software dar.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class JhoveServiceImpl implements JhoveService {
    
    private static final Logger LOGGER = new Logger(JhoveServiceImpl.class);

    private ConfigurationService configurationService;

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    

    @Override
    public File executeJhove(String pathToJhoveJar, String pathToInputFile, String pathToOutput, String nameOfSip, String extension) throws SystemException{
       
        File newReport = new File(pathToOutput, nameOfSip + ".jhove-log.txt");
        
        File jhoveJar = new File(pathToJhoveJar); // Pfad zum Programm JHove
        StringBuffer command = new StringBuffer("java -jar " + jhoveJar + " ");
        
        String jhoveConfig = getConfigurationService().getPathToJhoveConfiguration();
        
        // das passende Modul zur jeweiligen File-Extension auswählen
        if (extension.equals("txt")) {
            command.append("-m ascii-hul ");             
        } else if (extension.equals("gif")) {
            command.append("-m gif-hul ");             
        } else if (extension.equals("html")) {
            command.append("-m html-hul ");             
        } else if (extension.equals("jpg") || extension.equals("jpeg")) {
            command.append("-m jpeg-hul ");             
        } else if (extension.equals("jp2") || extension.equals("jpf") || extension.equals("jpx")) {
            command.append("-m jpeg2000-hul ");             
        } else if (extension.equals("pdf")) {
            command.append("-m pdf-hul ");             
        } else if (extension.equals("tif") || extension.equals("tiff") || extension.equals("tfx")) {
            command.append("-m tiff-hul ");             
        } else if (extension.equals("wav") || extension.equals("bwf")) {
            command.append("-m wave-hul ");             
        } else if (extension.equals("xml")) {
            command.append("-m xml-hul ");             
        } else {
            command.append("-m bytestream ");             
        }
        
        command.append(" -c "); 
        command.append("\""); 
        command.append(jhoveConfig); 
        command.append("\" "); 

        command.append("-o "); 
        command.append("\""); 
        command.append(newReport.getAbsolutePath()); 
        command.append("\""); 
        command.append(" "); 
        command.append(pathToInputFile); 
       
        try {
            
            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec(command.toString());

            // Fehleroutput holen
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");

            // Output holen
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");

            Util.switchOffConsole();
            
            // Threads starten
            errorGobbler.start();
            outputGobbler.start();

            // Warte, bis wget fertig ist
            proc.waitFor();
            
            
            int exitValue = proc.exitValue();
            Util.switchOnConsole();
                                 
           
        }
        catch (Exception e) {
            LOGGER.logDebug("JHove Service failed: " + e.getMessage());
            throw new SystemException(e.toString());
        }
       
        return newReport;

    }
    
   
}
