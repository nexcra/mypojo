package erwins.util.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import erwins.util.collections.AbstractMapSupport;
import erwins.util.guava.GsonUtil;
import erwins.util.text.StringUtil;


/** 
 * enum을 json으로 컨트롤해주기 위한 간이 서포터
 * */ 
public class EnumJsonSupport extends AbstractMapSupport<String,JsonObject>{
	
	protected Map<String,JsonObject> jsonMap = new ConcurrentHashMap<String,JsonObject>();
	private Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Enum.class, GsonUtil.generateEnumJsonSerializer(null)).create();
	
	public <T extends Enum<?>> void add(Class<T> clazz) {
		JsonArray array = (JsonArray) gson.toJsonTree(clazz.getEnumConstants()); 
		JsonObject map = new JsonObject();
		for(JsonElement each : array){
			JsonObject eachJson =  each.getAsJsonObject();
			String name = eachJson.get("name").getAsString(); //필수요소임 
			map.add(name, eachJson);
		}
		Object exist1 = jsonMap.put(clazz.getSimpleName(),map);
		Object exist2 = jsonMap.put(StringUtil.uncapitalize(clazz.getSimpleName()),map); 
		Preconditions.checkState(exist1 == null);
		Preconditions.checkState(exist2 == null);
	}
	
	/** code에 추가로 각종 옵션을 줄 수 있다.
	 * ex) select 박스에 전체 */
	@SuppressWarnings("unchecked")
	@Override
	public JsonObject get(Object key) {
		JsonObject json = jsonMap.get(key);
		if(json==null) json = new JsonObject(); //기본값 캐시해도 
		return json;
	}

	

}

