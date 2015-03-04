package erwins.util.guava;

import java.lang.reflect.Type;

import lombok.Data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/** 
 * 뎁스가 깊어지면 매핑이 안되는거 같음??  일단 대충 이렇게 사용 
 * */
@Data(staticConstructor="of")
public class DelegateJsonSerializer implements JsonSerializer<Object>{
	
	private final Gson gson;

	@Override
	public JsonElement serialize(Object page, Type arg1, JsonSerializationContext arg2) {
		return gson.toJsonTree(page);
	}
	
}
