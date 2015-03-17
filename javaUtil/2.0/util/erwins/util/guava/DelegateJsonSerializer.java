package erwins.util.guava;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/** 
 * Gson은 Vo에  인터페이스로 매핑되면 작동하지 않는다(리플렉션 하기 힘들어서????)
 * 암튼 잘 모르겠으니 이렇게 사용할것
 * */
@Data(staticConstructor="of")
public class DelegateJsonSerializer implements JsonSerializer<Object>{
	
	private final AtomicReference<Gson> gsonRef;

	@Override
	public JsonElement serialize(Object page, Type arg1, JsonSerializationContext arg2) {
		return gsonRef.get().toJsonTree(page);
	}
	
}
