package ch.bedag.a6z.sipvalidator.exception;

public class SystemException extends Exception {

    private static final long serialVersionUID = -5675144395744241578L;

    
    public SystemException() {
        super();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

}
