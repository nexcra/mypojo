
package erwins.util.web;

import java.io.*;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import erwins.util.lib.CharSets;
import erwins.util.lib.Strings;
import erwins.util.tools.TextFileReader;
import erwins.util.tools.TextFileReader.LineCallback;

/**
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class AjaxTool {
    
    /** 안씀. */
    //public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    
    public static final String IS_SUCCESS = "isSuccess";
    public static final String MESSAGE = "message";
    
    /**
     * 자바스크립트를 캐싱한다.  
     * \n 하는것 잊지말것
     */
    public static void writeScript(HttpServletResponse resp,File js,int second){
        cacheForSeconds(resp,second,false);
        resp.setContentType("application/javascript; charset="+CharSets.UTF_8);
        final PrintWriter out;
        try {
            out = resp.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(File each:js.listFiles()){
            String ext = Strings.getExtention(each.getName());
            if(!ext.equals("js")) continue;
            TextFileReader.read(each,new LineCallback(){
                public void process(String line) throws Exception {
                    out.write(line+"\n"); 
                }
            });
        }
    }
    
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_EXPIRES = "Expires";
    
    /**
     * Spring의 메소드를 가져옴. 
     */
    private static void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate){
        if (true) {
            response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + seconds * 1000L);
        }
        if (true) {
            // HTTP 1.1 header
            String headerValue = "max-age=" + seconds;
            if (mustRevalidate) {
                headerValue += ", must-revalidate";
            }
            response.setHeader(HEADER_CACHE_CONTROL, headerValue);
        }
    }
    
    
    
    /**
     * obj의 toString을 
     */
    public static void writeObject(HttpServletResponse resp,Object obj){
        getOut(resp).write(obj.toString());
    }

    /**
     * success를 구분하는 json형태로 리턴한다.
     */
    public static void write(HttpServletResponse resp,Object obj,boolean success){
        PrintWriter out = getOut(resp);
        JSONObject json = new JSONObject();
        json.put(IS_SUCCESS, success); //자바스크립트 isSuccess와 매칭된다.
        json.put(MESSAGE,obj);
        out.write(json.toString());
    }

    /**
     * HttpServletResponse의 예외를 래핑한다.
     */
    public static PrintWriter getOut(HttpServletResponse resp) {
        resp.setContentType("text/xml; charset="+CharSets.UTF_8);
        PrintWriter out;
        try {
            out = resp.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
    
    public static void write(HttpServletResponse resp,Object obj){
        write(resp,obj,true);
    }
    
    public static void write(HttpServletResponse resp){
        write(resp,"",true);
    }

}
