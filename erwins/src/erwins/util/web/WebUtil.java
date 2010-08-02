
package erwins.util.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import erwins.util.lib.CharSets;
import erwins.util.lib.Strings;

/**
 * 각종 비동기 Http통신에 필요한 도구 모음.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class WebUtil {
	
	public static final String CONTENT_TYPE_DOWNLOAD = "application/octet-stream";

	/**
	 * 멀티파트 리퀘스트인지 검사
	 */
	public static boolean isMultipartFormRequest(HttpServletRequest req) {
		return (Strings.nvl(req.getContentType()).toLowerCase().startsWith("multipart/form-data")) ? true : false;
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
		return Strings.isMatch(header, "Android","AppleWebKit","iPhon");
	}
	
	/** MIME으로 사용 가능한 타입들 */
	public static String accept(HttpServletRequest req) {
		return req.getHeader(ACCEPT);
	}

	/** 구형 IE에서 다운로드를 취소할때 나는 오류를 무시한다. */
	public static void download(HttpServletResponse response, File file,String contentType) {
	
		if (!file.exists())
			file = new File(CharSets.getEucKr(file.getAbsolutePath()));
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
	    return Strings.getUrlAndExtention(requestedUrl);
	}
	
	/*
	 * 		response.setContentType("application/pdf");
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + UiUtils.toLatin(file.getName()) + "\";");
		response.setHeader("Expires", "0");
	    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	    response.setHeader("Pragma", "public");
	 */

	

}
