
package erwins.util.morph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.*;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CollectionOfElements;

import erwins.util.lib.*;
import erwins.util.morph.anno.Hidden;
import erwins.util.morph.anno.OracleListString;
import erwins.util.root.*;
import erwins.util.tools.SearchMap;

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
    private <T extends Object> JSONArray getByList(List<T> list) {
        JSONArray jsonArray = new JSONArray();
        for (Object each : list){
            if(each instanceof Object[]){
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
        else if (entity instanceof List) return getByList((List<Object>) entity);
        else if(entity instanceof DomainObject){
            try {
                return getByDomain(entity);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }    
        }else throw new IllegalArgumentException(entity.getClass()+"is not required type");
    }

    /**
     * 도메인 객체임으로 Map이나 generic이 아닌 List는 고려하지 않는다. 
     */
    @SuppressWarnings("unchecked")
    private JSONObject getByDomain(Object entity) throws Exception{

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
                }else json.put(fieldName,getByDomain(obj));
            } else if (Sets.isInstanceAny(annos, OracleListString.class)) {
                Object obj = method.invoke(entity);
                if(!Hibernate.isInitialized(obj)) continue;
                json.put(fieldName, Sets.getOracleStr((List) obj));
            } else if (Sets.isInstanceAny(annos, OneToMany.class, CollectionOfElements.class,ManyToMany.class)) {
                JSONArray jsonArray = new JSONArray();
                Collection sublist = (Collection) method.invoke(entity);
                if(!Hibernate.isInitialized(sublist)) continue;
                for(Object object : sublist){
                    if (object instanceof String) jsonArray.add(object.toString()); //스트링 배열일경우 
                    else jsonArray.add(getByDomain(object));
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
            } else if (obj instanceof List) {
                JSON array = getByList((List)obj);
                json.put(key, array);
            }
        }
        return json;
    }
    
    /** 간단한 request 파싱에 사용하자. */
    public static String requestedJSON(HttpServletRequest request){
        SearchMap map = new SearchMap(request);
        return JDissolver.instance().getByMap(map).toString();
    }

}