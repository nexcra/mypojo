package erwins.util.guava;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import erwins.util.collections.AbstractMapSupport;
import erwins.util.text.StringUtil;


/** 
 * 
 * enum을 json(DB결과값이 아닌 메뉴등에 사용되는 STATIC값)으로 컨트롤해주기 위한 간이 서포터
 * 
 * */ 
public class EnumJsonSupport extends AbstractMapSupport<String,JsonArray>{
	
	protected Map<String,JsonArray> jsonMap = new ConcurrentHashMap<String,JsonArray>();
	private Gson gson;
	
	/** enum array 들을 name을 key로 가지는 map으로 저장해준다. */
	public <T extends Enum<?>> void add(Class<T> clazz) {
		JsonArray array = (JsonArray) gson.toJsonTree(clazz.getEnumConstants()); 
		Object exist1 = jsonMap.put(clazz.getSimpleName(),array);
		Object exist2 = jsonMap.put(StringUtil.uncapitalize(clazz.getSimpleName()),array); 
		Preconditions.checkState(exist1 == null);
		Preconditions.checkState(exist2 == null);
	}
	
	/** code에 추가로 각종 옵션을 줄 수 있다.
	 * ex) select 박스에 전체 */
	@Override
	public JsonArray get(Object key) {
		JsonArray json = jsonMap.get(key);
		if(json==null) json = new JsonArray(); //기본값 캐시해도 
		return json;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}
	

}

