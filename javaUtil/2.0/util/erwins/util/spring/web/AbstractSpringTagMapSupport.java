package erwins.util.spring.web;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;

import com.google.common.base.Splitter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import erwins.util.collections.AbstractMapSupport;
import erwins.util.lib.ReflectionUtil;
import erwins.util.spring.SpringConversions;


/** 
 * EL에서 MAP 형식으로 Enum을 HTML을 구성하도록 도와주는 헬퍼
 * jsp 자체를 잘 안쓰는 추세임으로 사용을 지양하자.
 * ex) <form:select path="password" items="${enum['BulkUploadDiv']}" />
 * ex) <form:select path="password" items="${enum['BulkUploadDiv|{name=BulkUploadDiv,eq={aa1:true},start={전체:all}}']}" />
 * ex) items="${enums['{name=tradeDiv,eq={chargeDiv=FOC,typeDiv=CHARGE}}|{name=tradeDiv,eq={chargeDiv=FOC,typeDiv=USE}}']}"
 * */ 
public abstract class AbstractSpringTagMapSupport<T> extends AbstractMapSupport<String,Map<String,String>>{
	
	private final JsonParser jsonParser = new JsonParser();
	private final Splitter splitter = Splitter.on("|").trimResults().omitEmptyStrings();

	/** 
	 * 스프링은 map의 key/value로 tag를 작성한다.
	 * json변환시 순서가 유지됨으로 ListOrderedMap 사용 
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> get(Object inputObj) {
		String inputKey = (String) inputObj;
		Map<String,String> tag = new ListOrderedMap();
		for(String input : splitter.split(inputKey)){
			appendDate(tag,input);
		}
		return tag;
	}

	private void appendDate(Map<String,String> tag,String inputKey) {
		boolean isJson = inputKey.startsWith("{");
		if(isJson){
			
			JsonObject config;
			try {
				config = jsonParser.parse(inputKey).getAsJsonObject();
			} catch (JsonSyntaxException e1) {
				throw new IllegalArgumentException(inputKey+" 는 올바른 JSON 형식이 아닙니다.");
			}
			
			addToMap(tag, config.get("start"));

			JsonElement eqCondition = config.get("eq"); //AND 조건이다.
			
			String name = config.get("name").getAsString();
			
			Collection<T> list = findByName(name);
			if(list==null) throw new IllegalArgumentException(inputKey+" 에 해당하는 자료가 존재하지 않습니다.");
			for(T each : list){
				boolean pass = true;
				if(eqCondition!=null){
					for(Entry<String, JsonElement> e : eqCondition.getAsJsonObject().entrySet()){
						Object enumValue = ReflectionUtil.findFieldValue(each, e.getKey());
						String compareValue = e.getValue().getAsString();
						if(enumValue==null) continue; //NULL은 비교가 안됨
						String enumValueForCompare = SpringConversions.TO_STRING_DEFAULT.convert(enumValue);
						if(enumValueForCompare.compareTo(compareValue) != 0){
							pass = false;
							break;
						}
					}
				}
				if(pass) addToMap(tag, each);
			}
			addToMap(tag, config.get("end"));
		}else{
			Collection<T> list = findByName(inputKey);
			if(list==null) throw new IllegalArgumentException(inputKey+" 에 해당하는 자료가 존재하지 않습니다.");
			for(T each : list) addToMap(tag, each);
		}
	}

	private void addToMap(Map<String, String> tag, JsonElement configDate) {
		if(configDate==null) return;
		JsonObject headerJson = configDate.getAsJsonObject();
		for(Entry<String, JsonElement> e : headerJson.entrySet()){
			tag.put(e.getKey(), e.getValue().getAsString());
		}
	}
	
	protected abstract Collection<T> findByName(String name);
	protected abstract void addToMap(Map<String, String> tag, T each);

}

