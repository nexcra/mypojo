package erwins.util.morph;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import erwins.util.lib.CollectionUtil;
import erwins.util.lib.StringUtil;

/** 하드코딩 방지용 간이파서.  */
public class RequestToMap  {
    
private static final String DOT = ".";
	
	/** 2단계 까지만 지원한다. 나머지는 몰라..  */
	@SuppressWarnings({"unchecked" })
	public Map<String, Object> toMap(HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String[]> reqMap =  req.getParameterMap();
		for(Entry<String, String[]> entry : reqMap.entrySet()){
			String key = entry.getKey();
			//if(key.endsWith("[]")) key = key.substring(0, key.length()-2);
			String[] values = entry.getValue();
			if(StringUtils.contains(key, DOT)){
				addData(map, key, values);
			}else{
				if(values==null) map.put(key, null);
				else if(values.length == 1) map.put(key, values[0]);
				else map.put(key, CollectionUtil.toList(values));
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private void addData(Map<String, Object> map, String key, String[] values) {
		String parentKey = StringUtil.getFirst(key, DOT);
		String subKey = StringUtil.getFirstAfter(key, DOT);
		List<Map<String, Object>> list =  (List<Map<String, Object>>) map.get(parentKey);
		if(list==null){
			list = new ArrayList<Map<String, Object>>();
			map.put(parentKey, list);
			for(String each : values){
				Map<String, Object> eachMap = new HashMap<String, Object>();
				eachMap.put(subKey, each);
				list.add(eachMap);
			}
		}else{
			if(list.size() != values.length) throw new RuntimeException("request count is not match : " + key);
			for(int i=0;i<values.length;i++){
				list.get(i).put(subKey, values[i]);
			}
		}
	}
	
	public Boolean getBoolean(HttpServletRequest req,String key){
		String value = req.getParameter(key);
		return MapToBeanRoot.toBoolean(value);
	}
	
	/* ================================================================================== */
	/*                                  이하는 기존 자료                                                                                              */
	/* ================================================================================== */
	

	@SuppressWarnings("rawtypes")
	public Map<String, Object> getMap(HttpServletRequest req,String prefix) {
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (!name.startsWith(prefix)) continue;
			String[] values = req.getParameterValues(name);
			String key = name.substring(prefix.length() + 1);
			if (values.length == 0) map.put(key, StringUtils.EMPTY);
			else if (values.length == 1) map.put(key, values[0]);
			else map.put(name, values);
		}
		return map;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> getMap(HttpServletRequest req) {
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String[] values = req.getParameterValues(name);
			if (values.length == 0) map.put(name, StringUtils.EMPTY);
			else if (values.length == 1) map.put(name, values[0]);
			else map.put(name, values);
		}
		return map;
	}

	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> getList(HttpServletRequest req,String prefix) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Enumeration e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (!name.startsWith(prefix)) continue;
			String[] values = req.getParameterValues(name);
			String key = name.substring(prefix.length() + 1);
			for (int i = 0; i < values.length; i++) {
				Map<String, Object> map = nullSafeGet(list, i);
				map.put(key, values[i]);
			}
		}
		return list;
	}

	private static Map<String, Object> nullSafeGet(List<Map<String, Object>> list, int index) {
		if (list.size() <= index) {
			int count = list.size() - index + 1;
			for (int i = 0; i < count; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				list.add(map);
			}
		}
		return list.get(index);
	}
    
}
