package erwins.util.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erwins.util.collections.map.RequestMap;

/**
 * 쿠키~
 * @author erwins(my.pojo@gmail.com)
 **/
public class CookieUtil{
    
    private HttpServletResponse resp;
    private HttpServletRequest req;
    private String  domain; // => 상위 도메인(뒤에서부터)만 포함 가능하다. *~~.net 등
    private String  path = "/";
    
    public CookieUtil(HttpServletRequest req){
        this.req = req;
    }
    public CookieUtil(HttpServletResponse resp,String domain,String path){
        this.resp = resp;
        this.domain = domain;
        this.path = path;
    }
    
    /** maxAge : 초단위. 60*60*24*365 이면 1년이다. TimeUnit을 사용하자. */ 
    public  void add(String key,String vlaue,int maxAge){
    	Cookie cookie = new Cookie(key,vlaue);
        cookie.setDomain(domain);  
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        resp.addCookie(cookie);  //  => URLEncoder를 사용하자
    }
    
    /** maxAge를 조절해주는게 아니라 없는걸 추가해준다. */
    public void remove(String key){
    	add(key,"",0);
    }
    
    /** 없으면 null을 리턴 */
    public  String get(String key){
    	Cookie[] cookies = req.getCookies();
    	if(cookies==null) return null;
    	for(Cookie cookie : cookies){
    		if(cookie.getName().equals(key)) return cookie.getValue();
    	}
    	return null;
    }
    
    public  RequestMap get(){
    	RequestMap map = new RequestMap();
        for(Cookie cookie : req.getCookies()){
            map.put(cookie.getName(), cookie.getValue());
        }
        return map; 
    }
}