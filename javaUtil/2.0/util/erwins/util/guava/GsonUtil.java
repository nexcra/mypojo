package erwins.util.guava;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.core.convert.converter.Converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import erwins.util.counter.MapIdCounter;
import erwins.util.lib.ReflectionUtil;
import erwins.util.spring.SpringConversions;

public abstract class GsonUtil {
	
	/**  aa{ a=10,b=5 }  이걸 a=15 이렇게 변경  */
	public static final JsonSerializer<MapIdCounter<?>> MAP_COUNTER_TO_SUM = new JsonSerializer<MapIdCounter<?>>() {
    	@Override
    	public JsonElement serialize(MapIdCounter<?> arg0, Type arg1, JsonSerializationContext arg2) {
    		return new JsonPrimitive(arg0.totalCount());
    	}
    };

    /** 
     * 기본 시리얼라이저로 운용하자.
     * 이거 설정뒤에 값을 오버라이드 하면 해당 오버라이드 ENUM만 따로 적용된다
     * ex) gsonBuilder.registerTypeHierarchyAdapter(Enum.class,GsonUtil.DEFAULT_ENUM_SERIALIZER );
     *   */
    public static final JsonSerializer<Enum<?>> DEFAULT_ENUM_SERIALIZER = new  JsonSerializer<Enum<?>>() {
        @Override
        public JsonElement serialize(Enum<?> jobDiv, Type arg1, JsonSerializationContext arg2) {
        	JsonObject obj = new JsonObject();
        	//기본값 2개 먼저 입력.
        	obj.addProperty("name", jobDiv.name());
        	obj.addProperty("ordinal", jobDiv.ordinal());
        	List<Field> fildes = ReflectionUtil.getAllDeclaredFields(jobDiv.getClass());
        	for(Field field : fildes){
        		Object value = ReflectionUtil.getField(field, jobDiv);
        		//toString 하는게 기본이다. 여길 커스터마이징 할려면 또다른 컨버터가 필요
        		obj.addProperty(field.getName(), value==null ? "" : value.toString() );
        	}
            return obj;
        }
    };
	
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
	
	/** 보통 js용으로 사용된다.\
	 * GsonBuilder의 registerTypeHierarchyAdapter로 등록해서 사용 */
	public static JsonSerializer<Enum<?>> generateEnumJsonSerializer(Converter<Object,Object> converter){
		@SuppressWarnings("unchecked")
		final Converter<Object,Object> innerConverter = (Converter<Object, Object>) (converter==null ? SpringConversions.TO_STRING_DEFAULT : converter);
		return new JsonSerializer<Enum<?>>() {
            @Override
            public JsonElement serialize(Enum<?> object, Type arg1, JsonSerializationContext arg2) {
            	List<Field> fields = ReflectionUtil.getAllDeclaredFields(object.getClass());
            	JsonObject json = new JsonObject();
            	for(Field each : fields){
            		Object value = ReflectionUtil.getField(each, object);
            		Object converted = innerConverter.convert(value);
            		String name = each.getName();
            		addObjectToJson(json, name, converted);
            	}
                return json;
            }
        } ;
	}
	
	/** 간단 입력기 */
	public static void addObjectToJson(JsonObject json, String name, Object converted) {
		if(converted instanceof JsonElement){
			json.add(name, (JsonElement)converted);
		}else if(converted instanceof Number){
			json.addProperty(name,(Number) converted);
		}else if(converted instanceof Boolean){
			json.addProperty(name,(Boolean) converted);
		}else{
			json.addProperty(name,converted.toString());
		}
	}
	
	

}
