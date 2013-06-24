package erwins.util.guava;

import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class GsonUtil {
	
	/** Map을 key,value로 플랫화 해준다. 즉 객체 내용을 List로 볼 수 있다. 주로 화면단에서 사용 */
	public static JsonArray toPrimitiveArray(JsonObject json){
		JsonArray array = new JsonArray();
		for(Entry<String,JsonElement> entry : json.entrySet()){
			JsonElement el = entry.getValue();
			if(!el.isJsonPrimitive()) continue;
			JsonObject line = new JsonObject();
			line.addProperty("key", entry.getKey());
			line.add("value", el.getAsJsonPrimitive());
			array.add(line);
		}
		return array;
	}

}
