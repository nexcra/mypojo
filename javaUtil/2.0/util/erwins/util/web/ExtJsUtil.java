
package erwins.util.web;

import javax.servlet.http.HttpServletRequest;

import erwins.util.lib.StringUtil;


public abstract class ExtJsUtil {
	
	/** object를 그대로 전송시 배열에 []가 붙어서 나온다. 이를 수정해준다.
	 * 자바스크립트 객체안에 빈값을 가져올 경우  배열에 공백문자 1개가 들어오는 현상을 null로 리턴하게 수정
	 * ex) mybatis 같은데서 다이나믹sql을 사용할때 != null 조건을 활용하기 위해서 사용된다. (보통 in 구문을 사용하기때문임) */
    public static String[] getParameterValues(HttpServletRequest req,String key){
    	String[] value = req.getParameterValues(key);
    	if(value==null) value =  req.getParameterValues(key+"[]");
    	//if(value !=null && value.length==1 && StringUtil.isEmpty(value[0])) return ArrayUtils.EMPTY_STRING_ARRAY;
    	if(value !=null && value.length==1 && StringUtil.isEmpty(value[0])) return null;
    	return value;
    }
}
