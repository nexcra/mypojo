
package erwins.util.lib;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * write 래핑
 */
public abstract class Writers {
    
    public static void write(HttpServletResponse response,Object obj){
        PrintWriter out = getWriter(response);
        out.write(obj.toString());
    }

    public static PrintWriter getWriter(HttpServletResponse response) {
        response.setContentType("text/xml; charset="+CharSets.UTF_8);
        PrintWriter out;
        try {
            out = response.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

}