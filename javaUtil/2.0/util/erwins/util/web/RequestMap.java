
package erwins.util.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/** e-mail링크 / 파일링크 등을 위한 map
 * 파일이름으로 부가정보를 설정하자 */
public class RequestMap<T>{
	/**  AAA/BBB/C/ 처럼 마지막에 /가 와야 한다. */
	protected final String url;
	protected String parameterKey;
	protected Map<String,T> map = new HashMap<String,T>();
	
	/** 요건 Rest방식일경우 */
	public RequestMap(String url){
		this.url = url;
	}
	public RequestMap(String url,String key){
		this.url = url;
		this.parameterKey = key;
	}
	public T get(HttpServletRequest req){
		String key = null;
		if(parameterKey==null){
			String inputUrl = req.getRequestURI();
			inputUrl.substring(url.length(), inputUrl.length()); // /가 짤리는지 테스트 
		}else key = req.getParameter(key);
		return map.get(key);
	}
	public void put(String key,T value){
		map.put(key, value);
	}

}
