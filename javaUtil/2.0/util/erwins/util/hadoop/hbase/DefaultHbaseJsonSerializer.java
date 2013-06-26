package erwins.util.hadoop.hbase;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import erwins.util.lib.ReflectionUtil;
import erwins.util.text.StringUtil;

/** 커스터마이징 해서 사용하자. */
@Deprecated
public class DefaultHbaseJsonSerializer {
	
	private Gson gson;
	
	public DefaultHbaseJsonSerializer(){
        GsonBuilder gsonBuilder  = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC,Modifier.TRANSIENT);
        gson = gsonBuilder.create();
	}
	
	public DefaultHbaseJsonSerializer(final String[] exclusive){
        GsonBuilder gsonBuilder  = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC,Modifier.TRANSIENT);
        gsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes arg0) {
				return StringUtil.isEquals(arg0.getName(), exclusive);
			}
			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		});
        gson = gsonBuilder.create();
	}
	
	/** 필요하다면 확장하자. */
	protected Gson getGson() {
		return gson;
	}
	
	public String serialize(Object obj){
		return getGson().toJson(obj);
	}
	
	public <T> T deserialize(String className,String json){
		Type typeOf = ReflectionUtil.forName(className);
    	return getGson().fromJson(json, typeOf);
	}

}
