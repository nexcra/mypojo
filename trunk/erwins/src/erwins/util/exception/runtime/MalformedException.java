package erwins.util.exception.runtime;

/**
 * 사용자의 HTML입력, 설정파일 등 클라이언트/서버 측에서의 잘못된 input값이 들어왔을 경우 던진다.
 */
public class MalformedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MalformedException() {
        super();
    }

    public MalformedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedException(String message) {
        super(message);
    }

    public MalformedException(Throwable cause) {
        super(cause);
    }
    
}
