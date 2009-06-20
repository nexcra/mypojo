
package erwins.util.tools;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * @author erwins(my.pojo@gmail.com)
 */
public class AjaxTool {

    private HttpServletResponse response;

    public void isSuccess() throws IOException {
        response.getWriter().write("1");
    }

    public void isFail() throws IOException {
        response.getWriter().write("0");
    }

}
