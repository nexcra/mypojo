
package erwins.util.vender.spring;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.support.WebContentGenerator;

import erwins.util.tools.SystemInfo;
import erwins.util.web.AjaxTool;

/**
 * 스프링을 이용해서 Root Context기준으로 자원을 얻는다.
 * @author erwins(my.pojo@gmail.com)
 */
public class CopyOfSpringResource extends WebContentGenerator{

    /** 6시간 */
    private int second = SystemInfo.isServer() ? 60*6 : 1;
    
    /**
     * web-Root를 기준으로 file을 가져온다.
     */
    public File getResource(String path) throws IOException{
        Resource jsFolderResource = new ServletContextResource(getServletContext(), path);
        return jsFolderResource.getFile();
    }

    @RequestMapping("/script.do")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        System.out.println(req.getContextPath());
        System.out.println(req.getServletPath());
        String defaultPath = File.separator + "WEB-INF" + File.separator;
        String path = defaultPath + req.getParameter("path");
        File file = getResource(path);
        AjaxTool.writeScript(resp, file,second);
    }
}
