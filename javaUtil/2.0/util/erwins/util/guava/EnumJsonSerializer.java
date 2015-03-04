package erwins.util.guava;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import lombok.Data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import erwins.util.lib.ReflectionUtil;

/**
 * 기본세팅은 enum을 포장해주지 않는다. 이렇게 수정
 *  */
@Data(staticConstructor="of")
public class EnumJsonSerializer implements JsonSerializer<Enum<?>>{
	
	private final Gson gson;
	//private String idKey = "id";

	@Override
	public JsonElement serialize(Enum<?> enumUnstance, Type arg1, JsonSerializationContext arg2) {
		JsonObject json = new JsonObject();
		List<Field> fields = ReflectionUtil.getAllDeclaredFields(enumUnstance.getClass());
		for(Field field : fields){
			Object value = ReflectionUtil.getField(field, enumUnstance);
			JsonElement jsonValue = gson.toJsonTree(value);
			json.add(field.getName(), jsonValue);
		}
		//json.add(idKey, new JsonPrimitive(enumUnstance.name()));  //name으로 자동 입력된다.
		return json;
	}
	
}

