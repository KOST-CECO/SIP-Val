package ch.bedag.a6z.sipvalidator.service;


/**
 * 
 * Interface für den Message Stack Service.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public interface MessageService extends Service {
    
    final static String ERROR = "0";
    final static String FATAL = "1";
    final static String INFO = "2";
    final static String WARN = "3";
    final static String DEBUG = "4";

    void logInfo(String message);
    void logDebug(String message);
    void logWarning(String message);
    void logError(String message);
    void logFatal(String message);
    void clear();
    void print();
}
