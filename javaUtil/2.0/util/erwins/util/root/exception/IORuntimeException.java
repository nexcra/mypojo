package erwins.util.root.exception;

import java.io.IOException;


/**
 * IOException을 래핑하는 런타임 예외
 * 필요할때만 catch해서 사용학기 위해 만들었다. 
 * LIB 류에서는 IO예외를 이걸로 래핑해서 던지자.
 */
@SuppressWarnings("serial")
public class IORuntimeException extends RuntimeException{

	public IORuntimeException(String message, IOException cause) {
		super(message, cause);
	}
	
	public IORuntimeException(IOException cause) {
		super(cause);
	}
	
	@Override
	public synchronized IOException getCause() {
		return (IOException) super.getCause();
	}

	
	
}