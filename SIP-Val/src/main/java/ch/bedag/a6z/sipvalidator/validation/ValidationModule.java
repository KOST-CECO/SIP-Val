package ch.bedag.a6z.sipvalidator.validation;

import java.io.File;

import ch.bedag.a6z.sipvalidator.exception.SipValidatorException;
import ch.bedag.a6z.sipvalidator.service.MessageService;
import ch.bedag.a6z.sipvalidator.service.Service;

/**
 * Dies ist das Interface für alle Validierungs-Module und 
 * vereinigt alle Funktionalitäten, die den jeweiligen Modulen
 * gemeinsam sind.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public interface ValidationModule extends Service {

    public boolean validate(File sipDatei) throws SipValidatorException;
    
    public MessageService getMessageService();

}
