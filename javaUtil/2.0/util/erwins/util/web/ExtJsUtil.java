
package erwins.util.web;

import javax.servlet.http.HttpServletRequest;


public abstract class ExtJsUtil {
	
	/** object를 그대로 전송시 배열에 []가 붙어서 나온다. 이를 수정해준다. */
    public static String[] getParameterValues(HttpServletRequest req,String key){
    	String[] value = req.getParameterValues(key);
    	if(value==null) value =  req.getParameterValues(key+"[]");
    	return value;
    }
}
