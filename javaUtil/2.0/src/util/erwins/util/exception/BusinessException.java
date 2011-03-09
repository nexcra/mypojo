package erwins.util.exception;

import java.text.MessageFormat;

/**
 * 업무로직상 사용자의 HTML입력, 설정파일 등 클라이언트/서버 측에서의 잘못된 input값이 들어왔을 경우 던진다.
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException() {
        super();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String pattern,Object ... arguments) {
        super(MessageFormat.format(pattern, arguments));
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
    
}
