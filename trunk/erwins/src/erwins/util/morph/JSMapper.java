
package erwins.util.morph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CollectionOfElements;

import erwins.util.lib.*;
import erwins.util.morph.anno.Hidden;
import erwins.util.morph.anno.OracleListString;
import erwins.util.root.*;

/**
 * Object로 서버사이드의 JsonObject를 생성한다. +로 사용자정의 필터를 추가한 버전이다. FCKEditor의 경우 json으로
 * 데이터를 박을려면 escape를 하면 안된다. 그러나 다른 일반적인 HTML에 박이는 데이터의 경우 escape를 반드시 해야 한다.
 * toString옵션에 1을 주면 들여쓰기 한다. 참고.
 * @author erwins(my.pojo@gmail.com)
 */
@Singleton
public class JSMapper {
    
    private JSMapper() {};
    
    private static JSMapper theInstance  = new JSMapper();
    
    public static JSMapper instance(){
        return theInstance;
    }

    /**
     * 특별한 변환 없이 그냥 사용하면 되는것.
     */
    private static final Class<?>[] STRING_TYPE = new Class<?>[] { String.class, BigDecimal.class, int.class, Integer.class, long.class,
            Long.class,Boolean.class,boolean.class };

    private List<JsonConfig> list = new CopyOnWriteArrayList<JsonConfig>();

    public JSMapper add(JsonConfig command) {
        list.add(command);
        return this;
    }

    public interface JsonConfig {
        public boolean run(JSONObject json, Method method, Class<?> returnType, Annotation[] annos);
    }

    /**
     * Flex등에서 사용한다. list가 0일 경우에는 클라이언트 에서 처리해 주자.
     */
    public <T extends Object> JSONArray getArray(List<T> list) {
        JSONArray jsonArray = new JSONArray();
        for (Object each : list)
            jsonArray.add(getObject(each));
        return jsonArray;
    }

    /**
     * Map과 Bean 2가지 타입을 지원한다.
     */
    @SuppressWarnings("unchecked")
    public JSONObject getObject(Object entity) {
        if (entity instanceof Map) return getByMap((Map<Object, Object>) entity);
        try {
            return getByObject(entity);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private JSONObject getByObject(Object entity) throws Exception{

        String fieldName = null;

        JSONObject json = new JSONObject();
        if (entity == null) return json;

        Method[] methods = entity.getClass().getMethods();
        Class returnType = null;

        for (Method method : methods) {

            String name = method.getName();

            fieldName = Strings.getFieldName(name);
            if (fieldName == null) continue;

            returnType = method.getReturnType();

            Annotation[] annos = method.getAnnotations();

            //애는 걸러준다.
            if (Sets.isInstanceAny(annos, Hidden.class)) continue;
            //입력 파라메터가 없는것만 가져온다.
            if(method.getParameterTypes().length!=0) continue;
            
            for (JsonConfig each : list) {
                if (each.run(json, method, returnType, annos)) continue;
            }

            if (Sets.isSameAny(returnType, STRING_TYPE)) {
                Object obj = method.invoke(entity);
                if (obj != null) json.put(fieldName, obj);
            }else if (returnType == Calendar.class) {
                Object obj = method.invoke(entity);
                if (obj != null) json.put(fieldName, Days.DATE.get((Calendar) obj));
            } else if (returnType == Date.class) {
                Object obj = method.invoke(entity);
                if (obj != null) json.put(fieldName, Days.DATE.get((Date) obj));
            } else if (returnType.isEnum()) {
                Enum num = (Enum) method.invoke(entity);
                if(num == null) continue;
                if(num instanceof Pair){
                    Pair pair = (Pair)num;
                    json.put(fieldName + "Name",pair.getName());
                    json.put(fieldName,pair.getValue());
                }
                json.put(fieldName + "Name", num.toString());
                json.put(fieldName, num.name());
            }else if (Sets.isInstanceAny(annos, ManyToOne.class)) {
                Object obj = method.invoke(entity);
                if(obj==null) continue; 
                if(!Hibernate.isInitialized(obj)){
                    if(obj instanceof EntityId){
                        EntityId temp = (EntityId)obj;
                        JSONObject proxy = new JSONObject();
                        proxy.put("id", temp.getId());
                        json.put(fieldName,proxy);    
                        //Flex 게시판 등의 단일 뎁스를 위해준비.
                        json.put(fieldName+"Id",temp.getId());
                    }
                }else json.put(fieldName,getByObject(obj));
            } else if (Sets.isInstanceAny(annos, OracleListString.class)) {
                Object obj = method.invoke(entity);
                if(!Hibernate.isInitialized(obj)) continue;
                json.put(fieldName, Sets.getOracleStr((List) obj));
            } else if (Sets.isInstanceAny(annos, OneToMany.class, CollectionOfElements.class)) {
                JSONArray jsonArray = new JSONArray();
                List sublist = (List) method.invoke(entity);
                if(!Hibernate.isInitialized(sublist)) continue;
                for (Object object : sublist) {
                    if (object instanceof String) jsonArray.add(object.toString()); //스트링 배열일경우 
                    else jsonArray.add(getByObject(object));
                }
                json.put(fieldName, jsonArray);
            }
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    private JSONObject getByMap(Map<Object, Object> map) {
        Object obj;
        Object key;
        JSONObject json = new JSONObject();

        for (Entry<Object, Object> entry : map.entrySet()) {
            obj = entry.getValue();
            key = entry.getKey();
            Class<?> clazz = obj.getClass();
            if (Sets.isEqualsAny(STRING_TYPE, clazz)) {
                json.put(key, obj);
            } else if (clazz == String[].class) { //request에서 받아올때 주로 사용~
                String[] temp = (String[]) obj;
                JSONArray jsonArray = new JSONArray();
                for (int index = 0; index < temp.length; index++) {
                    jsonArray.add(temp[index]);
                }
                json.put(key, jsonArray);
            } else if (obj instanceof Date) {
                json.put(key, Days.DATE_SIMPLE.get((Date) obj));
            } else if (clazz == Timestamp.class) { //iBatis 기본 Date유형
                Timestamp t = (Timestamp) obj;
                json.put(key, Days.DATE_SIMPLE.get(new Date(t.getTime()))); //???????? 확인 안해봄.. 몰라.
            }
        }
        return json;
    }

}