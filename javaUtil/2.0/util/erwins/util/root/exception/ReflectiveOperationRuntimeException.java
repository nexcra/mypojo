package erwins.util.root.exception;



/**
 * ReflectiveOperationException을 래핑하는 런타임 예외
 */
@SuppressWarnings("serial")
public class ReflectiveOperationRuntimeException extends RuntimeException{

	public ReflectiveOperationRuntimeException(String message, ReflectiveOperationException cause) {
		super(message, cause);
	}
	
	public ReflectiveOperationRuntimeException(ReflectiveOperationException cause) {
		super(cause);
	}
	
	@Override
	public synchronized ReflectiveOperationException getCause() {
		return (ReflectiveOperationException) super.getCause();
	}
	
}