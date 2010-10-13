
package erwins.util.morph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Parent;

import erwins.util.lib.Clazz;
import erwins.util.lib.Days;
import erwins.util.lib.Encoders;
import erwins.util.lib.Maths;
import erwins.util.lib.Sets;
import erwins.util.lib.Strings;
import erwins.util.morph.anno.Hidden;
import erwins.util.morph.anno.OracleListString;
import erwins.util.root.DomainObject;
import erwins.util.root.EntityId;
import erwins.util.root.Pair;
import erwins.util.root.Singleton;
import erwins.util.valueObject.ValueObject;

/**
 * Object로 서버사이드의 JsonObject를 생성한다. +로 사용자정의 필터를 추가한 버전이다. FCKEditor의 경우 json으로
 * 데이터를 박을려면 escape를 하면 안된다. 그러나 다른 일반적인 HTML에 박이는 데이터의 경우 escape를 반드시 해야 한다.
 * toString옵션에 1을 주면 들여쓰기 한다. 참고.
 * 추후 XML이 필요하다면 인터페이스화 하자.
 * @author erwins(my.pojo@gmail.com)
 */
@Singleton
public class JDissolver {
    
    private JDissolver() {};
    
    private static JDissolver theInstance  = new JDissolver();
    
    public static JDissolver instance(){
        return theInstance;
    }
    
    private List<JsonConfig> list = new CopyOnWriteArrayList<JsonConfig>();

    public JDissolver add(JsonConfig command) {
        list.add(command);
        return this;
    }

    public interface JsonConfig {
        public boolean run(JSONObject json, Method method, Class<?> returnType, Annotation[] annos);
    }

    /**
     * 특별한 변환 없이 그냥 사용하면 되는것.
     */
    private static final Class<?>[] STRING_TYPE = new Class<?>[] { String.class, BigDecimal.class, int.class, Integer.class, long.class,
            Long.class,Boolean.class,boolean.class };

    /**
     * Flex등에서 사용한다. list가 0일 경우에는 클라이언트 에서 처리해 주자.
     * key가 없는 객체일 경우 (Hibernate에서 Object[]로 얻어오는 경우)를 필터링 해준다.
     *   => 이경우 doube은 소수 2째자리에서 반올림해준다.
     */
    private <T extends Object> JSONArray getByList(Iterable<T> list) {
        JSONArray jsonArray = new JSONArray();
        for (Object each : list){
        	if(each==null) continue;
        	else if(each instanceof String) jsonArray.add(each);
        	else if(each instanceof Object[]){
                Object[] array = (Object[])each;
                int count = 0;
                JSONObject json = new JSONObject();
                for(Object obj:array){
                    if(obj instanceof Double) obj = Maths.round((Double)obj,2);
                    json.put("r"+count++, obj);
                }
                jsonArray.add(json);
            }else jsonArray.add(build(each)); 
        }
        return jsonArray;
    }

    /**
     * Map과 array, domain 3가지 타입을 지원한다.
     */
    @SuppressWarnings("unchecked")
    public JSON build(Object entity) {
        if (entity instanceof Map) return getByMap((Map<Object, Object>) entity);
        else if(entity instanceof DomainObject) return getByDomain(entity,true);
        else if (entity instanceof Iterable) return getByList((Iterable) entity);
        else throw new IllegalArgumentException(entity+" is not required type");
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
    
    /**
     * 외부접근도 가능. 도메인 객체임으로 Map이나 generic이 아닌 List는 고려하지 않는다. 
     * Hibernate등으로 인해 Javassist로 엮인 객체는 anno를 얻어오지 못한다. 따라서 가져오는 방법을 이중화 한다.
     */
    @SuppressWarnings("unchecked")
    public void getByDomain(JSONObject json,Object entity,boolean recursive){
    	
        String fieldName = null;
        if (entity == null) return;

        Method[] methods = entity.getClass().getMethods();
        Class returnType = null;

        for (Method method : methods) {

            String name = method.getName();

            fieldName = Strings.getFieldName(name);
            if (fieldName == null) continue;
            
            returnType = method.getReturnType();
            
            //애는 걸러준다.
            if(Sets.isAnnotationPresent(method, Hidden.class)) continue;
            //if (Sets.isInstanceAny(annos, Hidden.class)) continue;
            
            //입력 파라메터가 없는것만 가져온다.
            if(method.getParameterTypes().length!=0) continue;
            
            Annotation[] annos = method.getAnnotations();
            for (JsonConfig each : list) {
                if (each.run(json, method, returnType, annos)) continue;
            }
            
            try {
            	if ((String.class.isAssignableFrom(returnType))) { //이놈은 특별히 이스케이핑 해준다.
            		String obj = (String)method.invoke(entity);
				    if (obj != null) json.put(fieldName,Encoders.escapeFlex(obj));
            	}else if (Sets.isAssignableFrom(returnType, STRING_TYPE)) {
				    Object obj = method.invoke(entity);
				    if (obj != null) json.put(fieldName, obj);
				}else if (Calendar.class.isAssignableFrom(returnType)) {
				    Object obj = method.invoke(entity);
				    if (obj != null) json.put(fieldName, Days.DATE.get((Calendar) obj));
				} else if (Date.class.isAssignableFrom(returnType)) {
				    Object obj = method.invoke(entity);
				    if (obj != null) json.put(fieldName, Days.DATE.get((Date) obj)); //DATE_SIMPLE가 더 나은듯??
				} else if (ValueObject.class.isAssignableFrom(returnType)) {
					ValueObject obj = (ValueObject)method.invoke(entity);
				    if (obj != null) json.put(fieldName, obj.returnValue());
				} else if(Pair.class.isAssignableFrom(returnType)) {  //Enum보다 Pair를 먼저 체크한다.
				    Pair pair = (Pair)method.invoke(entity);
				    if (pair != null){
				        json.put(fieldName + "Name",pair.getName());
				        json.put(fieldName,pair.getValue());
				    }
				} else if (returnType.isEnum()) {
				    Enum num = (Enum) method.invoke(entity);
				    if(num == null) continue;
				    json.put(fieldName + "Name", num.toString());
				    json.put(fieldName, num.name());
				} else if (Sets.isAnnotationPresent(method, OracleListString.class)) {
				    Object obj = method.invoke(entity);
				    if(obj==null) continue;
				    if(!Hibernate.isInitialized(obj)) continue;
				    json.put(fieldName, Sets.getOracleStr((List) obj));            
				    /** Parent가 있을 경우 무한루프 방지. */
				}else if (recursive && !Sets.isAnnotationPresent(method, Parent.class) && 
						(Sets.isAnnotationPresent(method, ManyToOne.class) || DomainObject.class.isAssignableFrom(returnType)  )) {
				    Object obj = method.invoke(entity);
				    if(obj==null) continue;
				    if(obj instanceof EntityId){
			        	//캐스팅하면 id만 불러올때 세션을 읽어 쿼리를 날려버린다. (이전 버전에선 가능했다.) 따라서 리플렉션으로 불러오자.
			            Object id = Clazz.getObject(obj, EntityId.ID_NAME);
			            if(id==null) continue;
			            JSONObject proxy = new JSONObject();
			            proxy.put("id", id);
			            json.put(fieldName,proxy);
			            //Flex 게시판 등의 단일 뎁스를 위해준비.
			            json.put(fieldName+"Id",id);
			        }
				    //재귀 호출이라도 Hibernate가 init되지 않았다면 재귀를 멈춘다.
				    if(Hibernate.isInitialized(obj)) json.put(fieldName,getByDomain(obj,true));
				} else if (recursive && ( Sets.isAnnotationPresent(method, OneToMany.class, CollectionOfElements.class,ManyToMany.class)
					 || Collection.class.isAssignableFrom(returnType)	)) {
				    JSONArray jsonArray = new JSONArray();
				    Collection sublist = (Collection) method.invoke(entity);
				    if(sublist==null) continue;
				    if(!Hibernate.isInitialized(sublist)) continue;
				    for(Object object : sublist){
				    	if(object==null) continue;
				        if (object instanceof String) jsonArray.add(object.toString()); //스트링 배열일경우 
				        else jsonArray.add(getByDomain(object,true));
				    }
				    json.put(fieldName, jsonArray);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }
    }

    /** DB에서 null이 입력되면 Map으로 바꿔서 가져올때 null로 들어온다. 
     * Flex에서 null을 입력하면 인지하지 못한다. 따라서 ""로 입력한다.
     *  */
    @SuppressWarnings("unchecked")
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
            if (Sets.isEqualsAny(STRING_TYPE, clazz)) {
            	json.put(key, Encoders.escapeFlex(value.toString()));
            } else if (clazz == String[].class) { //request에서 받아올때 주로 사용~
                String[] temp = (String[]) value;
                JSONArray jsonArray = new JSONArray();
                for (int index = 0; index < temp.length; index++) {
                    jsonArray.add(temp[index]);
                }
                json.put(key, jsonArray);
            } else if (value instanceof Date) {
                json.put(key, Days.DATE_SIMPLE.get((Date) value));
            } else if (value instanceof List) {
                JSON array = getByList((List)value);
                json.put(key, array);
            }
        }
        return json;
    }
    
    /** 플렉스에서는 <>등이 오면 json을 인식하지 못한다. 이것들만 골라서 이스케이핑 해주자. */
	public static void escapeForFlex(JSON json){
		if(json instanceof JSONObject){
			JSONObject obj = (JSONObject)json;
			for(Object key : obj.keySet()){
				Object value = obj.get(key);
				if(value instanceof String) obj.put(key, Encoders.escapeXml2((String)value));
				else if(value instanceof JSON) escapeForFlex((JSON)value);
			}
		}else{
			JSONArray array = (JSONArray)json;
			for(int i=0;i<array.size();i++){
				Object each = array.get(i);
				if(each==null) continue;
				if(each instanceof JSON) escapeForFlex((JSON)each);
				else if(each instanceof String){
					String value = Encoders.escapeXml2((String)each);
					array.remove(i);
					array.add(i, value);
				}
			}
		}
	}    

}