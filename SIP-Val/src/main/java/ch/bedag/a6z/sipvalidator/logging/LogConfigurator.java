package ch.bedag.a6z.sipvalidator.logging;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import ch.bedag.a6z.sipvalidator.service.TextResourceService;

public class LogConfigurator implements MessageConstants {

    private static final ch.bedag.a6z.sipvalidator.logging.Logger LOGGER = new ch.bedag.a6z.sipvalidator.logging.Logger(
            LogConfigurator.class);

    private TextResourceService textResourceService;

    
    public TextResourceService getTextResourceService() {
        return textResourceService;
    }
    public void setTextResourceService(TextResourceService textResourceService) {
        this.textResourceService = textResourceService;
    }

    public String configure(String directoryOfLogfile, String nameOfLogfile) {
        
        String logFileName = directoryOfLogfile + File.separator + nameOfLogfile + ".validationlog.log"; 
        //String logFileName = directoryOfLogfile + File.separator + "sipvalidator-TEST.log";
        Logger rootLogger = Logger.getRootLogger();
        
        MessageOnlyLayout layout = new MessageOnlyLayout();
        try {
            FileAppender logfile = new FileAppender(layout, logFileName);
            logfile.setName("logfile");
            logfile.setAppend(false);
            logfile.activateOptions();
            
            rootLogger.addAppender(logfile);
            
        } catch (IOException e) {
            LOGGER.logInfo(getTextResourceService().getText(ERROR_LOGGING_NOFILEAPPENDER));            
        }
        
        return logFileName;
    }

}
