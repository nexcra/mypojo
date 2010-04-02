package erwins.util.exception;

/**
 * 배치 돌릴때 예외가 난 객체를 같이 묶어서 던진다.
 */
public class BatchException extends IllegalArgumentException {
	
	private Object exceptionedObject;

    private static final long serialVersionUID = 1L;

    public BatchException() {
        super();
    }

    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchException(String message) {
        super(message);
    }
    
    public BatchException(String message,Object exceptionedObject) {
        super(message);
        this.exceptionedObject = exceptionedObject;
    }    

    public BatchException(Throwable cause) {
        super(cause);
    }

	public Object getExceptionedObject() {
		return exceptionedObject;
	}

	public void setExceptionedObject(Object exceptionedObject) {
		this.exceptionedObject = exceptionedObject;
	}
    
}
