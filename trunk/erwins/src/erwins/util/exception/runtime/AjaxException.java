package erwins.util.exception.runtime;

/**
 * Ajax 사용중 에러가 았을때 사용한다. 사용하지 말것!
 */
public class AjaxException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AjaxException() {
        super();
    }

    public AjaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public AjaxException(String message) {
        super(message);
    }

    public AjaxException(Throwable cause) {
        super(cause);
    }
    
}
