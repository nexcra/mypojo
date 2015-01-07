
package erwins.util.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;

import erwins.util.root.exception.IORuntimeException;
import erwins.util.root.exception.PropagatedRuntimeException;
import erwins.util.text.CharEncodeUtil;
import erwins.util.text.StringUtil;

/**
 * 각종 비동기 Http통신에 필요한 도구 모음.
 */
public abstract class WebUtil {
	
	/** IE의 경우 <embed type="application/pdf" src="login.pdf.do" width="100%" height="100%"/> 를 사용 가능하다.
	 * excel은 안되는듯.. */
	public static final String CONTENT_TYPE_PDF = "application/pdf";
	public static final String CONTENT_TYPE_DOWNLOAD = "application/octet-stream";
	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
	private static final String POST = "POST";
	
	public static final String USER_AGENT = "user-agent";
	public static final String ACCEPT = "accept";

	/**
	 * 멀티파트 리퀘스트인지 검사
	 */
	public static boolean isMultipartFormRequest(HttpServletRequest req) {
		return (StringUtil.nvl(req.getContentType()).toLowerCase().startsWith(CONTENT_TYPE_MULTIPART)) ? true : false;
	}
	
	/** 모바일 기기(안드로이드/아이폰 등)에서 온 요청인지? */
	public static boolean isMobile(HttpServletRequest req) {
		String header = req.getHeader(USER_AGENT);
		return StringUtil.isMatchIgnoreCase(header, "Android","iPhon"); //크롬의 경우 AppleWebKit가 포함된다.
		//return StringUtil.isMatchIgnoreCase(header, "Android","AppleWebKit","iPhon");
		//return Strings.isMatch(header, "Android","AppleWebKit","iPhon");
	}
	
	/** MIME으로 사용 가능한 타입들 */
	public static String accept(HttpServletRequest req) {
		return req.getHeader(ACCEPT);
	}

	/** 파일이름설정과 다운로드를 별도 분리 */
	public static void downloadFile(HttpServletResponse response, File file) {
		if (!file.exists())
			file = new File(CharEncodeUtil.getEucKr(file.getAbsolutePath()));
		if (!file.exists())
			throw new IllegalArgumentException(file.getAbsolutePath() + " : file not found!");
	
		OutputStream out = null;
		FileInputStream fis = null;
		
		response.setContentLength((int) file.length());
	
		try {
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
			throw new IORuntimeException(e);
		} catch (Exception e) {
			throw new PropagatedRuntimeException(e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					throw new IORuntimeException(e);
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
			throw new IORuntimeException(e);
		}
    }
    
    public static int  getInt(HttpServletRequest req,String key){
    	String value = req.getParameter(key);
    	return Integer.valueOf(value);
    }
    public static long  getLong(HttpServletRequest req,String key){
    	String value = req.getParameter(key);
    	return Long.valueOf(value);
    }
    
    /** Poi처럼 resp에 write만 있는 경우 파일이름 등을 지정하기 위해 사용 */
    public static void  setFileName(HttpServletResponse resp,String fileName,String contentType){
    	resp.setContentType(contentType);
        //resp.setContentLength((int) file.length());
        try {
			resp.setHeader("Content-Disposition", "attachment; fileName=\""+ new String(fileName.getBytes("EUC_KR"), "8859_1") + "\";");
		} catch (UnsupportedEncodingException e) {
			throw new IORuntimeException(e);
		}
    	//resp.setHeader("Content-Disposition", "attachment; fileName=\""+ fileName + "\";");
        resp.setHeader("Content-Transfer-Encoding", "binary");
    }
    
    /** 디폴트값 입력 */
    public static void  setFileName(HttpServletResponse resp,String fileName){
    	setFileName(resp,fileName,CONTENT_TYPE_DOWNLOAD);
    }
    
    public static boolean isPost(HttpServletRequest req){
        return POST.equals(req.getMethod().toUpperCase());
    }
    
    public static boolean isAjax(HttpServletRequest req){
    	String xRequestWith = req.getHeader("x-requested-with");
    	if(xRequestWith==null) return false;
    	if( xRequestWith.equals("XMLHttpRequest")) return true;
    	return false;
    }
    
    /** 간단유틸 */
    @SuppressWarnings("unchecked")
	public static Map<String,String> getHeaderMap(HttpServletRequest req){
    	Map<String,String> map = Maps.newHashMap();
    	Enumeration<String> keys = req.getHeaderNames();
    	while(keys.hasMoreElements()){
    		String key = keys.nextElement();
    		map.put(key, req.getHeader(key));
    	}
    	return map;
    }
    
    
    /** WAS에서 L4나 웹서버의 IP가 찍히는 경우, 포워딩되기전 IP가 필요할때 */
	public static String getForwardedIp(HttpServletRequest req){
		String remoteIp = req.getHeader("x-forwarded-for");
    	return remoteIp;
    }
    
    
    
}
