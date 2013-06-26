package erwins.util.validation;

import java.text.MessageFormat;

import javax.validation.ValidationException;

/**
 * 업무로직상 사용자의 HTML입력, 설정파일 등 클라이언트/서버 측에서의 잘못된 input값이 들어왔을 경우 던진다.
 */
public class InputValidationException extends ValidationException {

	private static final long serialVersionUID = 1572373021430683671L;

	public InputValidationException() {
        super();
    }

    public InputValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputValidationException(String message) {
        super(message);
    }
    
    public InputValidationException(String pattern,Object ... arguments) {
        super(MessageFormat.format(pattern, arguments));
    }

    public InputValidationException(Throwable cause) {
        super(cause);
    }
    
}
