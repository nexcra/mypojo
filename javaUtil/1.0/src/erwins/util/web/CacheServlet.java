
package erwins.util.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import erwins.util.lib.Encoders;
import erwins.util.tools.SystemInfo;

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
 * @author erwins(my.pojo@gmail.com)
 */
public class CacheServlet extends HttpServlet{

    /** 6시간 */
    private int second = SystemInfo.isServer() ? 60*6 : 1;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String root = getServletConfig().getServletContext().getRealPath("WEB-INF");
        String path = root+ File.separator + req.getParameter("path");
        File file = new File(path);
        if(!file.isDirectory()){
            String message = this.getClass().getName() + "으로의 로딩이 실패했습니다. \n";
            message += file.getAbsolutePath() + " is not directory \n";
            message += "web.xml의 서블릿 매핑 설정을 확인하세요.";
            message = Encoders.escapeJavaScript(message);
            AjaxTool.writeToString(resp, "alert('"+message+"');");
        }
        else AjaxTool.writeScript(resp, file,second); 
    }
}
