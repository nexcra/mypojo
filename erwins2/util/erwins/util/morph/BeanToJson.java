package erwins.util.morph;

import java.lang.reflect.Field;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Hibernate;

import erwins.util.lib.ReflectionUtil;
import erwins.util.root.DomainObject;
import erwins.util.root.EntityId;
import erwins.util.root.Singleton;

/**
 */
@Singleton
public class BeanToJson extends BeanToJsonRoot {

	public static BeanToJson create() {
		final BeanToJson theInstance = new BeanToJson();
		theInstance.addConfig(STRING);
		theInstance.addConfig(DATE);
		theInstance.addConfig(VALUE_OBJECT);
		theInstance.addConfig(PAIR_OBJECT);
		//theInstance.addConfig(ENUM_OBJECT);
		theInstance.addConfig(new BeanToJSONBaseConfig(new Class[] { DomainObject.class }, new BeanToJSONConfigFetcher() {
			@Override
			public boolean fetch(Object instance, Field field, JSONObject json) {
				Object value =  ReflectionUtil.getField(field, instance);
				if(value==null) return true;
				String fieldName = field.getName();
				//재귀 호출이라도 Hibernate가 init되지 않았다면 재귀를 멈춘다.
				if(Hibernate.isInitialized(value)) json.put(fieldName,theInstance.getByDomain(value,true));
				else if(value instanceof EntityId){
		        	//캐스팅하면 id만 불러올때 세션을 읽어 쿼리를 날려버린다. (이전 버전에선 가능했다.) 따라서 리플렉션으로 불러오자.
		            Object id = ReflectionUtil.findFieldValue(value, EntityId.ID_NAME);
		            if(id==null) return true;
		            JSONObject proxy = new JSONObject();
		            proxy.put("id", id);
		            json.put(fieldName,proxy);
		            //Flex 게시판 등의 단일 뎁스를 위해준비.
		            json.put(fieldName+"Id",id);
		        }
				return true;
			}
		}));
		theInstance.addConfig(new BeanToJSONBaseConfig(new Class[] { Collection.class }, new BeanToJSONConfigFetcher() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean fetch(Object instance, Field field, JSONObject json) {
				Collection value =  (Collection)ReflectionUtil.getField(field, instance);
				if(value==null) return true;
				if(!Hibernate.isInitialized(value)) return true;
				JSONArray jsonArray = theInstance.getByList(value);
			    json.put(field.getName(), jsonArray);
				return true;
			}
		}));
		
		return theInstance;
	}
}