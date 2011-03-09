
package erwins.util.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erwins.domain.SystemInfo;

/**
 * ex) script src="/js.cache?path=js" type="text/javascript" / 
 * <servlet>
		<servlet-name>cacheServlet</servlet-name>
		<servlet-class>erwins.util.web.CacheServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>cacheServlet</servlet-name>
		<url-pattern>*.cache</url-pattern>
	</servlet-mapping>
 * 직접 서블릿으로 매칭되는 캐시
 */
@SuppressWarnings("serial")
public class CacheServlet extends HttpServlet{

    /** 6시간 */
    private int second = SystemInfo.isServer() ? 60*6 : 1;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String root = getServletConfig().getServletContext().getRealPath("WEB-INF");
        String path = root+ File.separator + req.getParameter("path");
        File file = new File(path);
        if(!file.isDirectory()) throw new RuntimeException("로딩이 실패했습니다. web.xml의 서블릿 매핑 설정을 확인하세요.");
        WebUtil.writeScript(resp, file,second); 
    }
}
