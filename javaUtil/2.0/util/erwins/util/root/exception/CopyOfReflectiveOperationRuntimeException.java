package erwins.util.root.exception;



/**
 * ㅅㅂ 1.7부터 되잔아.. 망할
 * ReflectiveOperationException을 래핑하는 런타임 예외
 */
@SuppressWarnings("serial")
public class CopyOfReflectiveOperationRuntimeException extends RuntimeException{

	public CopyOfReflectiveOperationRuntimeException(String message, ReflectiveOperationException cause) {
		super(message, cause);
	}
	
	public CopyOfReflectiveOperationRuntimeException(ReflectiveOperationException cause) {
		super(cause);
	}
	
	@Override
	public synchronized ReflectiveOperationException getCause() {
		return (ReflectiveOperationException) super.getCause();
	}
	
}