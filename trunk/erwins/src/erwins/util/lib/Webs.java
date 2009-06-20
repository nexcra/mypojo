package erwins.util.lib;


import javax.servlet.http.HttpServletRequest;

/**
 * request를 다룬다. 별 의미 없음.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class Webs {
    
    /**
     * 사용자 브라우저의 정보를 리턴한다.
     * ex) Mozilla/5.0 (Windows; U; Windows NT 5.1; ko; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5 GTB5,gzip(gfe)
     */
    public static String addIfNotFound(HttpServletRequest req) {
        return req.getHeader("User-Agent");
    }
    
}