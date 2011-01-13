
package erwins.util.morph;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.DayUtil;
import erwins.util.lib.MathUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringEscapeUtil;
import erwins.util.root.DomainObject;
import erwins.util.root.Pair;
import erwins.util.root.Singleton;
import erwins.util.valueObject.ValueObject;

/**
 * Object로 서버사이드의 JsonObject를 생성한다. +로 사용자정의 필터를 추가한 버전이다. FCKEditor의 경우 json으로
 * 데이터를 박을려면 escape를 하면 안된다. 그러나 다른 일반적인 HTML에 박이는 데이터의 경우 escape를 반드시 해야 한다.
 * toString옵션에 1을 주면 들여쓰기 한다. 참고.
 * @author erwins(my.pojo@gmail.com)
 */
@Singleton
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class BeanToJsonRoot {
    
    /**
     * 특별한 변환 없이 그냥 사용하면 되는것.
     */
    private static final Class<?>[] STRING_TYPE = new Class<?>[] {String.class, BigDecimal.class, int.class, Integer.class, long.class,
            Long.class,Boolean.class,boolean.class };
    
    protected static final BeanToJSONConfigFetcher STRING = new BeanToJSONBaseConfig(STRING_TYPE,
			new BeanToJSONConfigFetcher() {
		@Override
		public boolean fetch(Object instance,Field field, JSONObject map) {
			Object value = ReflectionUtil.getField(field, instance);
			if(value!=null) map.put(field.getName(), value);
			return true;
		}
	});
    protected static final BeanToJSONConfigFetcher DATE = new BeanToJSONBaseConfig(new Class[]{Date.class},
    		new BeanToJSONConfigFetcher() {
    	@Override
    	public boolean fetch(Object instance,Field field, JSONObject map) {
    		Date value =(Date) ReflectionUtil.getField(field, instance); 
    		if(value!=null) map.put(field.getName(), DayUtil.DATE.get(value));
			return true;
    	}
    });
    protected static final BeanToJSONConfigFetcher VALUE_OBJECT = new BeanToJSONBaseConfig(new Class[]{ValueObject.class},
    		new BeanToJSONConfigFetcher() {
    	@Override
    	public boolean fetch(Object instance,Field field, JSONObject map) {
    		ValueObject value =(ValueObject) ReflectionUtil.getField(field, instance); 
    		if(value!=null) map.put(field.getName(), value.returnValue());
    		return true;
    	}
    });
    
    /** Enum인데 Pair인게 있기 때문에 이놈이 먼저 온다. */
    protected static final BeanToJSONConfigFetcher PAIR_OBJECT = new BeanToJSONBaseConfig(new Class[]{Pair.class},
    		new BeanToJSONConfigFetcher() {
    	@Override
    	public boolean fetch(Object instance,Field field, JSONObject json) {
    		Pair value =(Pair) ReflectionUtil.getField(field, instance); 
    		if (value != null){
    			String fieldName = field.getName();
    	        json.put(fieldName + "Name",value.getName());
    	        json.put(fieldName,value.getValue());
    	    }
    		return true;
    	}
    });
    
    protected static final BeanToJSONConfigFetcher ENUM_OBJECT = new BeanToJSONConfigFetcher() {
    	@Override
    	public boolean fetch(Object instance,Field field, JSONObject json) {
    		if(!field.getType().isEnum()) return false;
    		Enum value =  (Enum) ReflectionUtil.getField(field, instance);
    		if (value != null){
    			String fieldName = field.getName();
    			json.put(fieldName + "Name", value.toString());
    		    json.put(fieldName, value.name());
    		}
    		return true;
    	}
    };
    
    
    private final List<BeanToJSONConfigFetcher> configs = new ArrayList<BeanToJSONConfigFetcher>();
	public void addConfig(BeanToJSONConfigFetcher config) {
		configs.add(config);
	}    
	
    /**
     * Map과 array, domain 3가지 타입을 지원한다. 프리미티브 등 단순 데이터는 허용하지 않는다.
     * DomainObject가 먼저 와야 나머지 Iterable과 중첩되더라도 먼저 실행된다.
     */
    public JSON build(Object entity) {
        if (entity instanceof DomainObject) return getByDomain(entity,true);
        else if (entity instanceof Map) return getByMap((Map<Object, Object>) entity);
        else if(entity instanceof Object[]){
        	Object[] array = (Object[])entity;
            int count = 0;
            JSONObject json = new JSONObject();
            for(Object obj:array){
                if(obj instanceof Double) obj = MathUtil.round((Double)obj,2);
                json.put("r"+count++, obj);
            }
            return json;
        }
        else if (entity instanceof Iterable) return getByList((Iterable) entity);
        else  return getByDomain(entity,true);
        //else if (entity instanceof Iterable) return getByList((Iterable) entity);
        //else throw new IllegalArgumentException(entity+" is not required type");
    }	

    /**
     * Flex등에서 사용한다. list가 0일 경우에는 클라이언트 에서 처리해 주자.
     * key가 없는 객체일 경우 (Hibernate에서 Object[]로 얻어오는 경우)를 필터링 해준다.
     *   => 이경우 doube은 소수 2째자리에서 반올림해준다.
     */
    public JSONArray getByList(Iterable<?> list) {
        JSONArray jsonArray = new JSONArray();
        for (Object each : list){
        	if(each==null) continue;
        	if(each instanceof String || each instanceof Number) jsonArray.add(each);
            else jsonArray.add(build(each)); 
        }
        return jsonArray;
    }

    /** json타입은 XML과는 달리 바디 부분에 key가 없는 value를 넣기 애매하다. 
     * 따라서 DB에서 하나의 컬럼만 가져와서 배열로 할당하는 경우 강제로 키를 지정해주자. */
    public JSONArray buildSingleList(List<Object> list,String key){
    	JSONArray jsonArray = new JSONArray();
    	for(Object each : list){
    		JSONObject json = new JSONObject();
    		json.put(key, each);
    		jsonArray.add(json);
    	}
    	return jsonArray;
    }


    /** 외부접근도 가능. 도메인 객체임으로 Map이나 generic이 아닌 List는 고려하지 않는다. */
    public JSONObject getByDomain(Object entity,boolean recursive){
    	JSONObject json = new JSONObject();
    	if (entity == null) return json;
    	getByDomain(json,entity,recursive);
    	return json;
    }
    
	/** class와 매칭되는걸 쉽게 찾게하기 위해 만든 헬퍼클래스 */
	public static class BeanToJSONBaseConfig implements BeanToJSONConfigFetcher {
		private final Class<?>[] ableClass;
		private final BeanToJSONConfigFetcher fetcher;

		public BeanToJSONBaseConfig(Class<?>[] ableClass, BeanToJSONConfigFetcher fetcher) {
			this.ableClass = ableClass;
			this.fetcher = fetcher;
		}

		@Override
		public boolean fetch(Object instance,Field field, JSONObject map) {
			if (CollectionUtil.isAssignableFrom(field.getType(), ableClass)) return fetcher.fetch(instance,field, map);
			return false;
		}
	}

	/** 성공여부를 리턴한다. true이면 성공적으로 값이 매칭된 것이다. (입력된게 아니다. null이면 무시할 수 있다) */
	public static interface BeanToJSONConfigFetcher {
		public boolean fetch(Object instance,Field field, JSONObject map);
	}    
    
	
    /**
     * 외부접근도 가능. 도메인 객체임으로 Map이나 generic이 아닌 List는 고려하지 않는다. 
     * Hibernate등으로 인해 Javassist로 엮인 객체는 anno를 얻어오지 못한다. 따라서 가져오는 방법을 이중화 한다.
     */
	public void getByDomain(JSONObject json,Object instance,boolean recursive){
        if (instance == null) return;
        List<Field> fields = ReflectionUtil.getAllDeclaredFields(instance.getClass());
        for (Field field : fields) {
        	field.setAccessible(true);
            for (BeanToJSONConfigFetcher each : configs) {
            	boolean isMatch = each.fetch(instance,field, json);
				if (isMatch) break;
            }
        }
    }

    /** DB에서 null이 입력되면 Map으로 바꿔서 가져올때 null로 들어온다. ???? 
     * Flex에서 null을 입력하면 인지하지 못한다. 따라서 ""로 입력한다. ???
     * 걍 시리얼 쓰는게 나아보임.
     *  */
    private JSONObject getByMap(Map<Object, Object> map) {
        Object value;
        Object key;
        JSONObject json = new JSONObject();

        for (Entry<Object, Object> entry : map.entrySet()) {
            value = entry.getValue();
            key = entry.getKey();
            if(value==null){
            	json.put(key, "");
            	continue;
            }
            Class<?> clazz = value.getClass();
            if (CollectionUtil.isEqualsAny(STRING_TYPE, clazz)) {
            	json.put(key, StringEscapeUtil.escapeFlex(value.toString()));
            } else if (clazz == String[].class) { //request에서 받아올때 주로 사용~
                String[] temp = (String[]) value;
                JSONArray jsonArray = new JSONArray();
                for (int index = 0; index < temp.length; index++) {
                    jsonArray.add(temp[index]);
                }
                json.put(key, jsonArray);
            } else if (value instanceof Date) {
                json.put(key, DayUtil.DATE_SIMPLE.get((Date) value));
            } else if (value instanceof List) {
                JSON array = getByList((List)value);
                json.put(key, array);
            }
        }
        return json;
    }
    
    /** 플렉스에서는 <>등이 오면 json을 인식하지 못한다. 이것들만 골라서 이스케이핑 해주자. 
     * 분명 성능에 문제있을듯.. ㅅㅂ
     * 왜 가본 JSON에 이 옵션이 없는지?*/
	public static void escapeForFlex(JSON json){
		if(json instanceof JSONObject){
			JSONObject obj = (JSONObject)json;
			for(Object key : obj.keySet()){
				Object value = obj.get(key);
				if(value instanceof String) obj.put(key, StringEscapeUtil.escapeXml2((String)value));
				else if(value instanceof JSON) escapeForFlex((JSON)value);
			}
		}else{
			JSONArray array = (JSONArray)json;
			for(int i=0;i<array.size();i++){
				Object each = array.get(i);
				if(each==null) continue;
				if(each instanceof JSON) escapeForFlex((JSON)each);
				else if(each instanceof String){
					String value = StringEscapeUtil.escapeXml2((String)each);
					array.remove(i);
					array.add(i, value);
				}
			}
		}
	}    

}