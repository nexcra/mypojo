package erwins.webapp.myApp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import erwins.util.morph.RequestToMap;

@Component
public class RequestToMapForApp  extends RequestToMap{

	/** EXT.js에서 
	 * null필드는 ''로 전달된다. key일 경우 이를 null로 치환해야 한다.
	 * 배열 필드 뒤에 []가 붙어서 온다. 이를 치환해주자. */
	@Override
	public Map<String, Object> toMap(HttpServletRequest req) {
		Map<String, Object> result = new HashMap<String, Object>(); 
		Map<String, Object> map = super.toMap(req);
		for(Entry<String, Object> entry : map.entrySet()){
			String key = entry.getKey();
			if("id".equals(key)){
				if("".equals(entry.getValue())) result.put(key, null);
				else result.put(key, entry.getValue());
			}else{
				if(key.endsWith("[]")) key = key.substring(0, key.length()-2);
				result.put(key, entry.getValue());	
			}
		}
		return result;
	}
	
	
	
}

