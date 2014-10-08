package erwins.util.root.exception;




/**
 * 배치 로직에서 text나 CSV를 읽어서 VO로 변경할때 던지는 예외
 */
@SuppressWarnings("serial")
public class TextParseException extends RuntimeException{

	public TextParseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TextParseException(String message) {
		super(message);
	}
	
}