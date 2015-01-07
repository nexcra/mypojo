package erwins.util.spring.batch;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public abstract class BatchParameterUtil {

	private static final Gson GSON = new Gson();

	/** 스프링 배치의 shortContext를 map으로 변경해준다. (GSON 버전) */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> shortContextToGMap(String shortContext) {
		Map<String, Object> result = Maps.newLinkedHashMap();
		if (Strings.isNullOrEmpty(shortContext))
			return result;

		Map<String, Object> json = GSON.fromJson(shortContext, Map.class);

		Object mapObject = json.get("map");
		if (mapObject == null)
			return result;
		if (mapObject instanceof StringMap) {
			StringMap<String> map = (StringMap<String>) mapObject;
			addStringMap(result, map);
		} else if (mapObject instanceof List) {
			List<StringMap> list = (List<StringMap>) mapObject;
			for (StringMap each : list)
				addStringMap(result, each);
		} else
			throw new IllegalStateException("알려지지 않은 타입입니다. " + mapObject);
		return result;
	}

	@SuppressWarnings("unchecked")
	private static void addStringMap(Map<String, Object> result, StringMap<String> map) {
		Object entry = map.get("entry");
		if (entry instanceof List) {
			List<Object> list = (List<Object>) entry;
			for (Object each : list)
				gsonObjectToValue(result, each);
		} else {
			gsonObjectToValue(result, entry);
		}
	}
	
	
	//================

	/** 스프링배치 컨텍스트를 GSON으로 읽은 값을, 일반 value로 변경해준다 **/
	@SuppressWarnings("unchecked")
	private static void gsonObjectToValue(Map<String, Object> result, Object each) {
		Preconditions.checkState(each instanceof StringMap, each.getClass().getSimpleName());

		StringMap<Object> map = (StringMap<Object>) each;
		int size = map.size();
		Preconditions.checkState(size == 1 || size == 2);

		if (size == 1) {
			List<Object> array = (List<Object>) map.get("string");
			Preconditions.checkState(array.size() == 2);
			Object value = array.get(1);
			// Gson은 타입을 지정안하면 1 -> 1.0 이렇게 변경해버린다. date형식이 숫자로 들어가는데 더블형태로 깨져서
			// 임시방편 처리한다.
			if (value instanceof Double)
				value = String.valueOf(((Double) value).longValue());
			result.put(array.get(0).toString(), value);
		} else {
			String key = (String) map.get("string");
			Object value = null;
			if (map.containsKey("int")) {
				value = map.get("int");
				if (value instanceof Double)
					value = ((Double) value).intValue();
			} else if (map.containsKey("long")) {
				value = map.get("long");
				if (value instanceof Double)
					value = ((Double) value).longValue();
			} else if (map.containsKey("double")) {
				value = map.get("double");// 요건 확인안됨
			}
			result.put(key, value);
		}
	}
	
    

}
