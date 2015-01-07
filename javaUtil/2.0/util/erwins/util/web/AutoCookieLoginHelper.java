
package erwins.util.web;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erwins.util.lib.security.MD5s;

/** 
 * 쿠키 기반 자동 로그인. public static final 으로 하나만 만들어놓은 후 쓰면 된다.
 * 시큐리티 사용하자 
 * */
@Deprecated
public class AutoCookieLoginHelper{
	
	/** 쿠기에 박아넣을 키값. */
	public final String cookieKey;
	/** 해시에 추가할 값으로, 아무 값이면 된다. */
	public final String hashSalt;
	
	//public final String serverIp;  ???????????
	public String cookiePath;
	public int maxAge = (int) TimeUnit.DAYS.toSeconds(30);
	
	public AutoCookieLoginHelper(String cookieKey,String hashSalt){
		this.cookieKey = cookieKey;
		this.hashSalt = hashSalt;
	}
	public AutoCookieLoginHelper setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
		return this;
	}
	public AutoCookieLoginHelper setMaxAge(int maxAge) {
		this.maxAge = maxAge;
		return this;
	}
	

	/** 자동로그인으로 설정되있다면 ID를 리턴한다. */
	public String getLoginIdByCookie(HttpServletRequest req) {
		Cookies cookie = new Cookies(req,null);
        String hashedValue = cookie.get(cookieKey);
        if(hashedValue==null) return null;
        
        String[] value = hashedValue.split("\\|");
        String id = value[0];
        if(MD5s.isMatch(value[1],hashSalt + id)) return id;
        return null;
    }
	
	/** 자동로그인으로 설정 */
	public void registCookie(HttpServletResponse resp,String id) {
		Cookies cookie = new Cookies(null,resp);
		if(cookiePath!=null) cookie.setPath(cookiePath);
		String value = id + "|" + MD5s.hash(hashSalt + id);
		cookie.add(cookieKey, value, maxAge);
	}
	
	/** 해제 */
	public void removeCookie(HttpServletResponse resp) {
		Cookies cookie = new Cookies(null,resp);
		if(cookiePath!=null) cookie.setPath(cookiePath);
		cookie.remove(cookieKey);
	}

}