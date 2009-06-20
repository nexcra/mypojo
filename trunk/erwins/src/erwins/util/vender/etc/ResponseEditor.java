
package erwins.util.vender.etc;



import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import erwins.util.lib.CharSets;

/**
 * response사용시의 XML, JSON 등의 유틸 모음
 * 이거 지울것.. ㅠㅠ
 */
public class ResponseEditor{
    
    protected PrintWriter out;
    //protected HttpServletResponse response;
    
    /**
     * UTF-8을 지원하지 못하는 특수 상황시 오버라이딩 할것. 
     */
    public String getCharSet(){
        return CharSets.UTF_8;
    }
    
    public ResponseEditor(HttpServletResponse response){
        //this.response = response;
        //response.setContentType("text/xml; charset=euc-kr");
        response.setContentType("text/xml; charset="+getCharSet());
        try {
            out = response.getWriter();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("runtime fail");
        }
    }

    public void write(Object obj){
        out.write(obj.toString());
    }
    
}
