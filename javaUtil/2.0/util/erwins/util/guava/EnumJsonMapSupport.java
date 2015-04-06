package erwins.util.guava;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import erwins.util.collections.AbstractMapSupport;
import erwins.util.spring.web.EnumFinder;


/** 
 * 
 * enum을 json(DB결과값이 아닌 메뉴등에 사용되는 STATIC값)으로 컨트롤해주기 위한 간이 서포터
 * jsp 자체를 잘 안쓰는 추세임으로 사용을 지양하자.
 * 
 * */ 
@Data
@EqualsAndHashCode(callSuper=false)
public class EnumJsonMapSupport extends AbstractMapSupport<String,JsonArray>{
	
	private EnumFinder enumFinder;
	private Gson gson;
	
	@Override
	public JsonArray get(Object key) {
		String inputKey = (String) key;
		List<Enum<?>> list = enumFinder.findBySimpleName(inputKey);
		JsonArray array =  gson.toJsonTree(list).getAsJsonArray();
		return array;
	}
	

}

