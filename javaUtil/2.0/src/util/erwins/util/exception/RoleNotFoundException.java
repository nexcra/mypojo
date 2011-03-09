package erwins.util.exception;

import erwins.util.lib.StringUtil;

/**
 * 로직 처리중 해당 메소드 호출에 대한 권한이 없을 경우 던진다. 
 * 비정상 적인 요청이다. 추가적으로 강제로 세션 종료 등의 로직을 가질 수 있다. 
 */
public class RoleNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public RoleNotFoundException() {
        super();
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RoleNotFoundException(String message,Object ... args) {
    	super(StringUtil.format(message, args));
    }

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(Throwable cause) {
        super(cause);
    }
    
}
