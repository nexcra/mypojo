package erwins.util.spring.web;

import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;

import com.google.common.base.Preconditions;

import erwins.util.spring.SpringUtil;
import erwins.util.text.StringUtil;


/** 
 * 스프링 태그가 언제까지 사용될지는 모르겠다. 
 * ex)
 * 
 * 	@PostConstruct
	public void init(){
		springTag.put("ALL", ALL);
		//add(BulkUploadDiv.class);
		add(OracleInstance.class);
	}
 * 
 * <form:select path="password" items="${enum['BulkUploadDiv']}" />
	  <form:radiobuttons path="userName" items="${enum['ALL|BulkUploadDiv']}" />
	  이 방법이 최선인지는.. 잘 모르겠다.
 * */ 
public class EnumSpringTagSupport extends AbstractSpringTagMapSupport{
	
	public <T extends Enum<?>> void add(Class<T> clazz) {
		Map<String,String> tag = SpringUtil.enumToSpringTagMap(clazz);
		Object exist1 = springTag.put(clazz.getSimpleName(),tag);
		Object exist2 = springTag.put(StringUtil.uncapitalize(clazz.getSimpleName()),tag); 
		Preconditions.checkState(exist1 == null);
		Preconditions.checkState(exist2 == null);
	}
	
	/** code에 추가로 각종 옵션을 줄 수 있다.
	 * ex) select 박스에 전체 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> get(Object inputObj) {
		String inputKey = (String) inputObj;
		
		Map<String,String> tag = springTag.get(inputKey);
		if(tag !=null) return tag;
		
		Map<String,String> compositTag = new ListOrderedMap();
		Iterable<String> keys = splitter.split(inputKey);
		for(String key : keys){
			Map<String,String> exist = springTag.get(key);
			Preconditions.checkState(exist != null , key +" 에 해당하는 태그가 존재하지 않습니다");
			compositTag.putAll(exist);
		}
		return compositTag;
	}

	

}

