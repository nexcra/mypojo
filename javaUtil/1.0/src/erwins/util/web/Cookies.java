package erwins.util.web;

import javax.servlet.http.*;

import erwins.util.tools.Mapp;

/**
 * 쿠키~
 * @author erwins(my.pojo@gmail.com)
 **/
public class Cookies{
    
    private HttpServletResponse resp;
    private HttpServletRequest req;
    
    public Cookies(HttpServletResponse resp){
        this.resp = resp;
    }
    public Cookies(HttpServletRequest req){
        this.req = req;
    }
    
    public  void add(String key,String vlaue){
        Cookie cookie = new Cookie("name","value");
        //cookie.setDomain("~~");  // => 상위 도메인(뒤에서부터)만 포함 가능하다. *~~.net 등
        //cookie.setMaxAge(maxAge);
        resp.addCookie(cookie);  //  => URLEncoder를 사용하자
    }
    
    public  void remove(String key){
        for(Cookie cookie : req.getCookies()){
            if(cookie.getName().equals(key)) cookie.setMaxAge(0);
        }
    }
    
    public  Mapp get(){
        Mapp map = new Mapp();
        for(Cookie cookie : req.getCookies()){
            map.put(cookie.getName(), cookie.getValue());
        }
        return map; 
    }
}