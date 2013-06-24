package erwins.util.morph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringUtil;


/** Gson 버전으로 새로 만들었다. 전부 Gson으로 위임. 일단 기본으로 대충 다 되는듯.. 
 * 뒷자리로 배열구분을 하게 되어있으나, reflection으로 하는게 더 정확할듯 하다. 일단 그냥 놔둠 */
public class MapMarshaller {
	
	private Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC,Modifier.TRANSIENT).create();
	
	public <T> T toBean(HttpServletRequest req,Class<T> clazz){
		@SuppressWarnings("unchecked")
		Map<String,Object> marshaled = marshal(req.getParameterMap(),clazz);
		JsonElement json = gson.toJsonTree(marshaled);
    	T vo =  gson.fromJson(json, clazz);
    	return vo;
	}
	
    public Map<String,Object> marshal(Map<String,String[]> requestMap,Class<?> clazz){
    	Map<String,Object> body = Maps.newHashMap();
    	Table<String,String,String[]> subCollection = HashBasedTable.create();
    	Table<String,String,String> subObject = HashBasedTable.create();
    	
    	for(Entry<String,String[]> entry : requestMap.entrySet()){
    		String key = entry.getKey();
    		String[] value = entry.getValue();
    		if(StringUtil.contains(key, '.')){
    			String[] subKey = StringUtil.getExtentions(key);
    			if(value.length > 1 ) subCollection.put(subKey[0], subKey[1], value);
        		else if(value.length > 0) {
        			Field field = ReflectionUtil.findField(clazz, subKey[0]);
        			if(field==null) continue;
        			if(Collection.class.isAssignableFrom(field.getType())) subCollection.put(subKey[0], subKey[1], value);
        			else subObject.put(subKey[0], subKey[1], value[0]);
        		}
    		}else{
    			if(key.endsWith("[]")) key = key.substring(0,key.length()-2); //ext에서 array object를 보낼때 []가 붙는다??
        		if(value.length > 1) body.put(key, value);
        		else if(value.length > 0) body.put(key, value[0]);
    		}
    	}
    	
    	for(Entry<String, Map<String, String[]>> entry: subCollection.rowMap().entrySet() ){
    		List<Map<String, String>> list = Lists.newArrayList();
    		Map<String, String[]> columns = entry.getValue();
    		boolean first = true;
    		for(Entry<String, String[]> each : columns.entrySet()){
    			String[] datas = each. getValue();
    			if(first){
    				for(int i=0;i<datas.length;i++) list.add(new HashMap<String, String>());
    				first = false;
    			}
    			for(int i=0;i<datas.length;i++){
    				String data = datas[i];
    				Map<String, String> map = list.get(i);
    				map.put(each.getKey(), data);
    			}
    		}
    		body.put(entry.getKey(), list);
    	}
    	for(Entry<String, Map<String, String>> entry: subObject.rowMap().entrySet() ){
    		Map<String, String> map = Maps.newHashMap();
    		for(Entry<String, String> each : entry.getValue().entrySet()){
    			map.put(each.getKey(), each.getValue());
    		}
    		body.put(entry.getKey(), map);
    	}
    	
    	return body;
    	
    }

    
    //============================== getter / setter ==========================================

	public void setGson(Gson gson) {
		this.gson = gson;
	}
    
    


    

}
