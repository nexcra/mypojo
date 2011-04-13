package erwins.jsample;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import erwins.util.web.WebUtil;

/**
 * Applet등을 이용한 업로드 서블릿
 * 이 Applet은 세션을 찾지 못한다.. 따라서 session에 해당하는 작업은 할 수 없다. 
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UploadServlet() {
        super();
    }

    /**
     * Url로 업로드할 위치를 넘겨받는다. 'private'제외
     * UploadProgressListener에 세션 처리 안해도 되겠지?
     */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String urlBody = WebUtil.getUrlAndExtention(request)[0];
	    if(urlBody.indexOf("..") > -1)
	        throw new IOException(MessageFormat.format("[{0}]은 비정상적인 경로입니다.", urlBody));
	    new UploadFileListener(request, response, urlBody).setListener(new UploadProgressListener());
	}
	
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    System.out.println("get은 안되요");
	}

}
