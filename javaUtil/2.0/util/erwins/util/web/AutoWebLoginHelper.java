
package erwins.util.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erwins.util.lib.security.MD5s;

/** 쿠키 기반 자동 로그인. public static final 으로 하나만 만들어놓은 후 쓰면 된다. */
public class AutoWebLoginHelper{
	
	/** 쿠기에 박아넣을 키값. */
	public final String cookieKey;
	/** 해시에 추가할 갑으로, 아무 값이면 된다. */
	public final String hashString;
	
	public final String serverIp;
	public final String cookiePath;
	public final int maxAge;
	
	public AutoWebLoginHelper(String cookieKey,String hashString,String serverIp,String cookiePath,int maxAge){
		this.cookieKey = cookieKey;
		this.hashString = hashString;
		this.serverIp = serverIp;
		this.cookiePath = cookiePath;
		this.maxAge = maxAge;
	}
/*
	*//** 자동로그인으로 설정되있다면 ID를 리턴한다. *//*
	public String autoLoginByCookie(HttpServletRequest req) {
        CookieUtil cookie = new CookieUtil(req);
        String hashedValue = cookie.get(cookieKey);
        if(hashedValue==null) return null;
        
        String[] value = hashedValue.split("\\|");
        String id = value[0];
        if(MD5s.isMatch(value[1],hashString + id)) return id;
        return null;
    }
	
	*//** 자동로그인으로 설정 *//*
	public void registCookie(HttpServletResponse resp,String id) {
		CookieUtil cookie = new CookieUtil(resp,serverIp,cookiePath);
		String value = id + "|" + MD5s.hash(hashString + id);
		cookie.add(cookieKey, value, maxAge);
	}
	
	*//** 해제 *//*
	public void removeCookie(HttpServletResponse resp) {
		CookieUtil cookie = new CookieUtil(resp,serverIp,cookiePath);
		cookie.remove(cookieKey);
	}*/

}