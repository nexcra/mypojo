
package erwins.util.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import erwins.util.collections.map.SearchMap;
import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.StringUtil;
import erwins.util.morph.BeanToJson;
import erwins.util.root.StringCallback;
import erwins.util.tools.TextFileReader;

/**
 * 각종 비동기 Http통신에 필요한 도구 모음.
 */
public abstract class WebUtil {
	
	/** IE의 경우 <embed type="application/pdf" src="login.pdf.do" width="100%" height="100%"/> 를 사용 가능하다.
	 * excel은 안되는듯.. */
	public static final String CONTENT_TYPE_PDF = "application/pdf";
	public static final String CONTENT_TYPE_DOWNLOAD = "application/octet-stream";
	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";

	/**
	 * 멀티파트 리퀘스트인지 검사
	 */
	public static boolean isMultipartFormRequest(HttpServletRequest req) {
		return (StringUtil.nvl(req.getContentType()).toLowerCase().startsWith(CONTENT_TYPE_MULTIPART)) ? true : false;
	}

	/** response에 file을 담아서 출력한다. 기본적으로 application/octet-stream로 되어있다. */
	public static void download(HttpServletResponse response, File file) {
		download(response,file,CONTENT_TYPE_DOWNLOAD);
	}
	
	public static final String USER_AGENT = "user-agent";
	public static final String ACCEPT = "accept";
	
	/** 모바일 기기(안드로이드/아이폰 등)에서 온 요청인지? */
	public static boolean isMobile(HttpServletRequest req) {
		String header = req.getHeader(USER_AGENT);
		return StringUtil.isMatchIgnoreCase(header, "Android","AppleWebKit","iPhon");
		//return Strings.isMatch(header, "Android","AppleWebKit","iPhon");
	}
	
	/** MIME으로 사용 가능한 타입들 */
	public static String accept(HttpServletRequest req) {
		return req.getHeader(ACCEPT);
	}

	/** 구형 IE에서 다운로드를 취소할때 나는 오류를 무시한다. */
	public static void download(HttpServletResponse response, File file,String contentType) {
	
		if (!file.exists())
			file = new File(CharEncodeUtil.getEucKr(file.getAbsolutePath()));
		if (!file.exists())
			throw new RuntimeException(file.getAbsolutePath() + " : file not found!");
	
		OutputStream out = null;
		FileInputStream fis = null;
	
		response.setContentType(contentType);
		response.setContentLength((int) file.length());
	
		try {
			// MS익스플러어가 기본적으로 8859_1를 인식하기때문에 변환을 해주어야 한다.
			response.setHeader("Content-Disposition", "attachment; fileName=\""
					+ new String(file.getName().getBytes("EUC_KR"), "8859_1") + "\";");
			response.setHeader("Content-Transfer-Encoding", "binary");
			/*
			response.setHeader("Expires", "0");
		    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		    response.setHeader("Pragma", "public");*/
	
			out = response.getOutputStream();
			fis = new FileInputStream(file);
			IOUtils.copy(fis, out);
			out.flush();
		} catch (IOException e) {
			// if(!e.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException"))
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}
	}

	/**
	 * Url은 '/'를 포함하는 root부터 시작한다. ex) /D:/qwe.qwe.go => 'D:/qwe.qwe' and 'go'
	 */
	public static String[] getUrlAndExtention(HttpServletRequest req) {
	    String requestedUrl = req.getRequestURI().substring(req.getContextPath().length());
	    return StringUtil.getUrlAndExtention(requestedUrl);
	}
	
	/**
     * 자바스크립트를 캐싱한다. \n 하는것 잊지말것
     */
    public static void writeScript(HttpServletResponse resp, File js, int second) {
        cacheForSeconds(resp, second, false);
        resp.setContentType("application/javascript; charset=" + CharEncodeUtil.UTF_8);
        final PrintWriter out;
        try {
            out = resp.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (File each : js.listFiles()) {
            String ext = StringUtil.getExtention(each.getName());
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
    public static void cacheForSeconds(HttpServletResponse response, int seconds, boolean mustRevalidate) {
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
    
    /** 간단한 request 파싱에 사용하자. */
    public static String requestedJSON(HttpServletRequest request){
        SearchMap map = new SearchMap(request);
        return BeanToJson.create().build(map).toString();
    }
    
    /** 서버로 접속 가능한 URL을 만든다. */
    public static String rootUrl(HttpServletRequest request){
    	return request.getServerName()+":"+request.getServerPort();
    }
    
    /** 접속 상대경로를 리턴한다. */
    public static String getUrl(HttpServletRequest request){
    	return request.getRequestURI().substring(request.getContextPath().length());
    }
    

    /**
     * 사용자 브라우저의 정보를 리턴한다.
     * ex) Mozilla/5.0 (Windows; U; Windows NT 5.1; ko; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5 GTB5,gzip(gfe)
     */
    public static String addIfNotFound(HttpServletRequest req) {
        return req.getHeader("User-Agent");
    }
    
    /** 루트의 WEB-INF 경로를 리턴한다. */
    public static File getRoot(ServletContext context,String path) {
    	String pathName = context.getRealPath(StringUtil.nvl(path,"/"));
    	return new File(pathName);
    }
    
    /** 웹루트를 리턴한다. */
    public static File getRoot(HttpServletRequest req,String path) {
    	return getRoot(req.getSession().getServletContext(),path);
    }
    
    /** 루트의 WEB-INF 경로를 리턴한다. */
    public static File getWebInfoRoot(HttpServletRequest req) {
    	return getRoot(req,"WEB-INF");
    }
    /** 루트의 WEB-INF 경로를 리턴한다. */
    public static File getWebInfoRoot(ServletContext context) {
    	return getRoot(context,"WEB-INF");
    }
    
    public static PrintWriter  getWriter(HttpServletResponse resp){
    	try {
			return resp.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}
