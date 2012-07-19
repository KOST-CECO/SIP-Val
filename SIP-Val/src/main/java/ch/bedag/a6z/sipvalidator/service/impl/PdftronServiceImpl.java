package ch.bedag.a6z.sipvalidator.service.impl;

import java.io.File;
import ch.bedag.a6z.sipvalidator.exception.SystemException;
import ch.bedag.a6z.sipvalidator.logging.Logger;
import ch.bedag.a6z.sipvalidator.service.PdftronService;
import ch.bedag.a6z.sipvalidator.service.TextResourceService;
import ch.bedag.a6z.sipvalidator.util.StreamGobbler;
import ch.bedag.a6z.sipvalidator.util.Util;
/**
 * Dieser Service stellt die Schnittstelle zur Pdftron Software dar.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class PdftronServiceImpl implements PdftronService {
    
    private static final Logger LOGGER = new Logger(PdftronServiceImpl.class);

    private TextResourceService textResourceService;

    public TextResourceService getTextResourceService() {
        return textResourceService;
    }
    public void setTextResourceService(TextResourceService textResourceService) {
        this.textResourceService = textResourceService;
    }
    
    @Override
    public String executePdftron(String pathToPdftronExe, String pathToInputFile, String pathToOutput, String nameOfSip) throws SystemException{
       
        File report;    
        File pdftronExe = new File(pathToPdftronExe); // Pfad zum Programm Pdftron
        File output = new File(pathToOutput);
        StringBuffer command = new StringBuffer(pdftronExe + " ");
        
        command.append("-l B "); 
        command.append("-o "); 
        command.append("\""); 
        command.append(output.getAbsolutePath()); 
        command.append("\""); 
        command.append(" "); 
        //command.append("\""); 
        command.append(pathToInputFile); 
        //command.append("\""); 
       
       
                
        //LOGGER.logDebug("Using pdftron command: " + command.toString());

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
            
            //System.out.println("exit value: " + exitValue);
            
            // Der Name des generierten Reports lautet per default report.xml und es scheint keine
            // Möglichkeit zu geben, dies zu übersteuern.
            report = new File(pathToOutput, "report.xml");
            File newReport = new File(pathToOutput, nameOfSip + ".pdftron-log.xml");
            
            // falls das File bereits existiert, z.B. von einem vorhergehenden Durchlauf, löschen wir es
            if (newReport.exists()) {
                newReport.delete();
            }
            
            boolean renameOk = report.renameTo(newReport);
            if (!renameOk) {
                throw new SystemException("Der Report konnte nicht umbenannt werden.");
            }
            report = newReport;
            
            // das gleichzeitig erzeugte Stylesheet löschen
            File styleSheetToDelete = new File(pathToOutput, "report.xsl");
            styleSheetToDelete.delete();
           
        }
        catch (Exception e) {
            LOGGER.logDebug("Pdftron Service failed: " + e.getMessage());
            throw new SystemException(e.toString());
        }
    
        
        return report.getAbsolutePath();

    }
    
    
    
    
    
    @Override
    public String getPathToInputFile() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getPathToPdftronExe() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setPathToInputFile(String pathToInputFile) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void setPathToPdftronExe(String pathToPdftronExe) {
        // TODO Auto-generated method stub
        
    }

   
}
