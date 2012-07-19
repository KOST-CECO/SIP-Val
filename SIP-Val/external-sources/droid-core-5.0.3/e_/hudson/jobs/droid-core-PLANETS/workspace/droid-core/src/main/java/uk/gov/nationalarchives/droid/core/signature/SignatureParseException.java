/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.signature;


/**
 * @author rflitcroft
 *
 */
public class SignatureParseException extends Exception {
    

    private static final long serialVersionUID = -7609762285665681819L;

    /**
     * Constructs a new SignatureFileException with a message. 
     * @param message the message
     */
    public SignatureParseException(String message) {
        super(message);
    }

    /**
     * Constructs a new SignatureFileException with a message. 
     * @param message the message
     * @param filePath 
     * @param cause the cause of the exception 
     */
    public SignatureParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
