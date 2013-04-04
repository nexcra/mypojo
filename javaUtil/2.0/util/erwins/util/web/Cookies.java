package erwins.util.web;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;

import erwins.util.root.Pair;
import erwins.util.root.PairObject;

/**
 * 쿠키~
 * domain (> 상위 도메인(뒤에서부터)만 포함 가능하다. *~~.net 등) 이런거 다 삭제
 **/
public class Cookies implements Iterable<Entry<String,String>>{
	
	private static final Pair P3P = new PairObject("P3P","CP='ALL CURa ADMa DEVa TAIa OUR BUS IND PHY ONL UNI PUR FIN COM NAV INT DEM CNT STA POL HEA PRE LOC OTC'");
    
	private final HttpServletResponse resp;
    private final HttpServletRequest req;
    private Map<String,String> map;
    private String path = "/";
    private int defaultmaxAge = (int) TimeUnit.DAYS.toSeconds(1);
    
    /** IE의 iframe에서 쿠키를 읽기 위해서는 쿠키 입력전 다음 세팅이 필요하다. */
    public Cookies p3pConfig(){
    	resp.setHeader(P3P.getName(), P3P.getValue());
    	return this;
    }
    
    public Cookies(HttpServletRequest req,HttpServletResponse resp){
        this.req = req;
        this.resp = resp;
    }
    
    /** maxAge : 초단위. 60*60*24*365 이면 1년이다. TimeUnit을 사용하자. */ 
    public  void add(String key,Object vlaue,int maxAge){
    	if(vlaue==null) vlaue = "";
    	Cookie cookie = new Cookie(key,vlaue.toString());
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        resp.addCookie(cookie);  //  => URLEncoder를 사용하자
    }
    public  void add(String key,Object vlaue){
    	add(key, vlaue, defaultmaxAge);
    }
    
    /** maxAge를 조절해주는게 아니라 없는걸 추가해준다. */
    public void remove(String key){
    	add(key,"",0);
    }
    
    /** req.getCookies()에 시간순으로 쿠키가 리턴되는것으로 추정된다.  */
    public  String get(String key){
    	if(map==null) map = toMap();
    	return map.get(key);
    }

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return toMap().entrySet().iterator();
	}

	/** 앞에 있는 만료된 쿠기?들은 오버라이드 된다  **/
    public Map<String, String> toMap() {
        Map<String, String> map = Maps.newHashMap();
        Cookie[] cookies = req.getCookies();
        if(cookies==null) return map;
    	for(Cookie cookie : cookies)  map.put(cookie.getName(), cookie.getValue());
        return map;
    }

	public void setPath(String path) {
		this.path = path;
	}

	public void setDefaultmaxAge(int defaultmaxAge) {
		this.defaultmaxAge = defaultmaxAge;
	}
	
	

}