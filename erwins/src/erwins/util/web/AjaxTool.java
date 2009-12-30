
package erwins.util.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.util.lib.CharSets;
import erwins.util.lib.Encoders;
import erwins.util.lib.Strings;
import erwins.util.morph.JDissolver;
import erwins.util.root.StringCallback;
import erwins.util.tools.TextFileReader;

/**
 * 각종 비동기 Http통신에 필요한 도구 모음.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class AjaxTool {

    private static Log log = LogFactory.getLog(AjaxTool.class);

    /** 안씀. */
    //public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    public static final String IS_SUCCESS = "isSuccess";
    public static final String MESSAGE = "message";

    /**
     * 자바스크립트를 캐싱한다. \n 하는것 잊지말것
     */
    public static void writeScript(HttpServletResponse resp, File js, int second) {
        cacheForSeconds(resp, second, false);
        resp.setContentType("application/javascript; charset=" + CharSets.UTF_8);
        final PrintWriter out;
        try {
            out = resp.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (File each : js.listFiles()) {
            String ext = Strings.getExtention(each.getName());
            if (!ext.equals("js")) continue;
            new TextFileReader().read(each, new StringCallback() {
                public void process(String line) {
                    out.write(line + "\n");
                }
            });
        }
    }

    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_EXPIRES = "Expires";

    /** Spring의 메소드를 도용 */
    private static void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
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
    public static void writeObject(HttpServletResponse resp, Object obj) {
        getOut(resp).write(obj.toString());
    }

    /**
     * success를 구분하는 json형태로 리턴한다. 
     * Flex의 경우 json타입이라 할지라도 XML구문이 들어오면 파싱 실패 오류를 낸다. 따라서 단순 문자열일경우 escape해 준다.
     * 자바스크립트의 경우 escape를 하면 그대로.. 출력된다.
     */
    public static void write(HttpServletResponse resp, Object obj, boolean success) {
        PrintWriter out = getOut(resp);
        JSONObject json = new JSONObject();
        json.put(IS_SUCCESS, success); //자바스크립트 isSuccess와 매칭된다.

        if (obj instanceof String) json.put(MESSAGE, Encoders.escapeFlex(obj.toString()));
        else if(obj instanceof JSON) json.put(MESSAGE, obj);
        else {
            JSON array = JDissolver.instance().build(obj);
            json.put(MESSAGE, array);
        }
        out.write(json.toString());
        log.debug(json);
    }

    /**
     * HttpServletResponse의 예외를 래핑한다.
     */
    public static PrintWriter getOut(HttpServletResponse resp) {
        resp.setContentType("text/xml; charset=" + CharSets.UTF_8);
        PrintWriter out;
        try {
            out = resp.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public static void write(HttpServletResponse resp, Object obj) {
        write(resp, obj, true);
    }
    public static void write(HttpServletResponse resp, String message,Object ... obj) {
        write(resp, MessageFormat.format(message, obj), true );
    }

    public static void write(HttpServletResponse resp) {
        write(resp, "", true);
    }
    
    /**
     * 사용자 브라우저의 정보를 리턴한다.
     * ex) Mozilla/5.0 (Windows; U; Windows NT 5.1; ko; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5 GTB5,gzip(gfe)
     */
    public static String addIfNotFound(HttpServletRequest req) {
        return req.getHeader("User-Agent");
    }
    
    /** 웹루트를 리턴한다. */
    public static File getRoot(HttpServletRequest req,String path) {
    	String pathName = req.getSession().getServletContext().getRealPath(Strings.nvl(path,"/"));
    	return new File(pathName);
    }
    
    /** 루트의 WEB-INF 경로를 리턴한다. */
    public static File getWebInfoRoot(HttpServletRequest req) {
    	return getRoot(req,"WEB-INF");
    }

}
