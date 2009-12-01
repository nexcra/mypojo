package erwins.util.exception;

/**
 */
public class NotSupportedMethodException extends SecurityException {

    private static final long serialVersionUID = 1L;

    public NotSupportedMethodException() {
        super();
    }

    public NotSupportedMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedMethodException(String message) {
        super(message);
    }

    public NotSupportedMethodException(Throwable cause) {
        super(cause);
    }
    
}
