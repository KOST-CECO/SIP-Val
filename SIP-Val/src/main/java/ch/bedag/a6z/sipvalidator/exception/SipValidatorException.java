package ch.bedag.a6z.sipvalidator.exception;

/**
 * 
 * Superklasse aller Applikations-Exceptions
 * 
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */

public class SipValidatorException extends Exception {

    private static final long serialVersionUID = -8666753675661449719L;

    public SipValidatorException() {
        super();
    }

    public SipValidatorException(String message) {
        super(message);
    }

}
