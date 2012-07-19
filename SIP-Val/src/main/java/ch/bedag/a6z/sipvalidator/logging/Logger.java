package ch.bedag.a6z.sipvalidator.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;

/**
 * Logging Klasse. Sämtliche Log Aufrufe werden an Jakarta Commons Logging
 * delegiert, welches diese wiederum an Log4j delegiert.
 * 
 * <p>
 * (c) Copyright Bedag Informatik 2010
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class Logger {

    /** Apache Commons Log. */
    private Log log;

    /**
     * Instanzierung eines benannten Loggers. Der Name des Loggers entspricht
     * dem Namen der übergebenen Klasse. Sinn: Beim Einsatz der Apache Log4j API
     * können über das Konfig File Package oder Klassenfilter gesetzt werden.
     * 
     * @param clazz
     *            Class for which a log name will be derived.
     */
    public Logger(Class<?> clazz) {

        // Log Instanz über Factory holen.
        this.log = LogFactory.getLog(clazz);
    }

    /**
     * Logt einen Fehler der die Stabilität des Programms beeinflusst.
     * 
     * @param message
     *            Fehlermeldung.
     */
    public void logFatal(String message) {
        this.log.fatal(message);

    }

    /**
     * Logt einen Fehler der die Stabilität des Programms beeinflusst.
     * 
     * @param message
     *            Fehlermeldung.
     * @param t
     *            Ursache des Fehlers.
     */
    public void logFatal(String message, Throwable t) {
        this.log.fatal(message, t);
    }

    /**
     * Logt einen Fehler, der nicht automatisch behoben werden kann.
     * 
     * @param message
     *            Fehlermeldung.
     */
    public void logError(String message) {
        this.log.error(message);
    }

    /**
     * Logt einen Fehler, der nicht automatisch behoben werden kann.
     * 
     * @param message
     *            Fehlermeldung.
     * @param t
     *            Ursache des Fehlers.
     */
    public void logError(String message, Throwable t) {
        this.log.error(message, t);
    }

    /**
     * Logt einen Fehler, der behoben oder übergangen werden konnte.
     * 
     * @param message
     *            Meldung.
     */
    public void logWarning(String message) {
        this.log.warn(message);
    }

    /**
     * Logt einen Fehler, der behoben oder übergangen werden konnte.
     * 
     * @param message
     *            Meldung.
     * @param t
     *            Ursache des Warnung.
     */
    public void logWarning(String message, Throwable t) {
        this.log.warn(message, t);
    }

    /**
     * Logt eine Information zum Programmablauf.
     * 
     * @param message
     *            Meldung.
     */
    public void logInfo(String message) {
        this.log.info(message);
    }

    /**
     * Logt eine Information zum Programmablauf.
     * 
     * @param message
     *            Meldung.
     * @param t
     *            Ursache.
     */
    public void logInfo(String message, Throwable t) {
        this.log.info(message, t);
    }

    /**
     * Logt eine Information zum Nachvollziehen des Programmstatus.
     * 
     * @param message
     *            Meldung.
     */
    public void logDebug(String message) {
        this.log.debug(message);
    }

    /**
     * Logt eine Information zum Nachvollziehen des Programmstatus.
     * 
     * @param message
     *            Meldung.
     * @param t
     *            Ursache.
     */
    public void logDebug(String message, Throwable t) {
        this.log.debug(message, t);
    }

    /**
     * Ist Logging auf Level Fatal aktiv?
     * 
     * @return true, falls aktiv, ansonsten false.
     */
    public boolean isFatalEnabled() {
        return this.log.isFatalEnabled();
    }

    /**
     * Ist Logging auf Level Error aktiv?
     * 
     * @return true, falls aktiv, ansonsten false.
     */
    public boolean isErrorEnabled() {
        return this.log.isErrorEnabled();
    }

    /**
     * Ist Logging auf Level Warn aktiv?
     * 
     * @return true, falls aktiv, ansonsten false.
     */
    public boolean isWarnEnabled() {
        return this.log.isWarnEnabled();
    }

    /**
     * Ist Logging auf Level Info aktiv?
     * 
     * @return true, falls aktiv, ansonsten false.
     */
    public boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    /**
     * Ist Logging auf Level Debug aktiv?
     * 
     * @return true, falls aktiv, ansonsten false.
     */
    public boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }

    /**
     * Setzen des aktuellen Debug - Contextes ( Log4J ).
     * 
     * @param theRemoteUser
     *            das aktuell angemeldete Benutzer
     */
    public void setDebugContext(String theRemoteUser) {
        NDC.push(theRemoteUser);
    }

    /**
     * Freigabe des aktuellen Debug - Contextes ( Log4J ).
     */
    public void unsetDebugContext() {
        NDC.pop();
        NDC.remove();
    }
}
