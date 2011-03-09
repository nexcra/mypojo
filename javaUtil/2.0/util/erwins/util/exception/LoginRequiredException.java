package erwins.util.exception;

import java.text.MessageFormat;

/**
 * 로직 처리중 해당 메뉴의 권한이 로그인이 필요할 경우 진다. 
 * 페이지 이동에만 해당한다. 로그인 하는 JSP로 이동시킬것~
 */
public class LoginRequiredException extends BusinessException {

    private static final long serialVersionUID = 1L;
    private String url; //로그인 페이지로 리다이렉트하기 전 기억할 요청 URL

    public LoginRequiredException() {
        super();
    }

    public LoginRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public LoginRequiredException(String pattern,Object ... arguments) {
        super(MessageFormat.format(pattern, arguments));
    }    

    public LoginRequiredException(String message) {
        super(message);
    }

    public LoginRequiredException(Throwable cause) {
        super(cause);
    }

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
    
}
