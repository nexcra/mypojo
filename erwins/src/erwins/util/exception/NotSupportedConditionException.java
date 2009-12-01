package erwins.util.exception;

import java.text.MessageFormat;

/**
 * 해당 명령을 진행할 수 없는 환경일 경우 발생한다.
 * MalformedException와는 달리 입력값을 수정해도 정상적인 행위가 불가능하다.
 * ex) FK가 있는데 삭제 명령을 내릴 경우
 */
public class NotSupportedConditionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotSupportedConditionException() {
        super();
    }

    public NotSupportedConditionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedConditionException(String message) {
        super(message);
    }
    
    public NotSupportedConditionException(String pattern,Object ... arguments) {
        super(MessageFormat.format(pattern, arguments));
    }

    public NotSupportedConditionException(Throwable cause) {
        super(cause);
    }
    
}
