package erwins.util.root.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * 래핑할께 없을때 최종으로 던지는거. throw new RuntimeException(e); 을 제거하기 위해서 사용한다. 
 * 이게 있으면 안된다. 
 */
@SuppressWarnings("serial")
public class PropagatedRuntimeException extends NestedRuntimeException{

	public PropagatedRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PropagatedRuntimeException(Throwable cause) {
		super("unexpected exception",cause);
	}
	
}