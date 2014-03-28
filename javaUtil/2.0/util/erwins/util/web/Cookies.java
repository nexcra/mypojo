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
	private static Base64s BASE_64 = new Base64s();
	
	private final HttpServletResponse resp;
    private final HttpServletRequest req;
    private Map<String,String> map;
    private String path = "/";
    private int defaultmaxAge = (int) TimeUnit.DAYS.toSeconds(1);
    boolean base64Encode = true;
    
    /** IE의 iframe에서 쿠키를 읽기 위해서는 쿠키 입력전 다음 세팅이 필요하다. */
    public Cookies p3pConfig(){
    	if(resp!=null) resp.setHeader(P3P.getName(), P3P.getValue());
    	return this;
    }
    
    /** 읽기나 쓰기 전용이라면 한쪽에 null을 넣으면 된다. */
    public Cookies(HttpServletRequest req,HttpServletResponse resp){
        this.req = req;
        this.resp = resp;
    }
    
    /** maxAge : 초단위. 60*60*24*365 이면 1년이다. TimeUnit을 사용하자. */ 
    public  void add(String key,Object vlaue,int maxAge){
    	String strValue =  vlaue==null ? "" : vlaue.toString();
    	if(base64Encode) strValue = BASE_64.encode(strValue);
    	Cookie cookie = new Cookie(key,strValue.toString());
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
    
    /** 원래 들어가있는 값과, 내가 임의로 넣은 값을 분리해서 가져올 수 있게 구분. */
    public  String getDecode(String key){
    	String value = get(key);
    	return value==null ? null : BASE_64.decode(value);
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
    	for(Cookie cookie : cookies) {
    		String strValue = cookie.getValue();
    		map.put(cookie.getName(), strValue);
    	}
        return map;
    }

	public void setPath(String path) {
		this.path = path;
	}

	public void setDefaultmaxAge(int defaultmaxAge) {
		this.defaultmaxAge = defaultmaxAge;
	}

	public boolean isBase64Encode() {
		return base64Encode;
	}

	public void setBase64Encode(boolean base64Encode) {
		this.base64Encode = base64Encode;
	}
	
	

}