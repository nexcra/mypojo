package erwins.util.morph;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import erwins.util.lib.*;
import erwins.util.morph.Mapping.MappingType;
import erwins.util.morph.anno.Hidden;
import erwins.util.root.Singleton;


/**
 * Object로 서버사이드의 JsonObject를 생성한다.
 * +로 사용자정의 필터를 추가한 버전이다.
 * FCKEditor의 경우 json으로 데이터를 박을려면 escape를 하면 안된다.
 * 그러나 다른 일반적인 HTML에 박이는 데이터의 경우 escape를 반드시 해야 한다.
 * toString옵션에 1을 주면 들여쓰기 한다. 참고.
 * @author erwins(my.pojo@gmail.com)
 */
@Singleton
public class JsonMapper{
    
    /** 플렉스용 name */
    private static final String LABEL = "label"; 
    private static final String VALUE = "value";
    private static final Class<?>[] STRING_TYPE = new Class<?>[]{String.class,BigDecimal.class
        ,int.class,Integer.class,long.class,Long.class};
    
    private List<JsonConfig> list = new CopyOnWriteArrayList<JsonConfig>();
    
    public JsonMapper add(JsonConfig command){
        list.add(command);
        return this;
    }
    
    public interface JsonConfig{
        public boolean run(JSONObject json,Method method,Class<?> returnType,Annotation[] annos);
    }
    
    public JsonMapper(){};
    
    /**
     * Flex등에서 사용한다. list가 0일 경우에는 클라이언트 에서 처리해 주자. 
     */
    public <T extends Object> JSONArray get(List<T> list){
        JSONArray jsonArray = new JSONArray();
        for(Object each: list) jsonArray.add(get(each));
        return jsonArray;
    }
    
    /**
     * Enum 타입의 class를 제작한다.
     * toString의 값이 이름이 되며
     * name 값이 DB에 저장되는 value값이 된다.
     * ordinal의 값을 사용할려면 다른 방법을 찾아보자. name방법이 추가/삭제/가시성에 좀더 자유롭다.
     */
    @SuppressWarnings("unchecked")
    public JSONArray get(Enum<?>[] en,boolean isAll, Enum<?> ... ingnor) {
        JSONArray jsonArray = new JSONArray();
        if(isAll) jsonArray.add(emptyJson());
        for (Enum<?> mode : en) {
            if(Sets.isEquals(ingnor, mode)) continue;
            JSONObject json = new JSONObject();
            //code.setIdByInt(mode.ordinal());
            json.put("ordinal", mode.ordinal());
            json.put(LABEL, mode.toString());
            json.put(VALUE, mode.name());  //name할려다가~
            jsonArray.add(json);
        }
        return jsonArray;
    }
    
    private JSONObject emptyJson(){
        JSONObject json = new JSONObject();
        json.put(LABEL, "전체");
        json.put(VALUE,"");
        return json;
    }
    
    /**
     * Map과 Bean 2가지 타입을 지원한다. 
     */
    @SuppressWarnings("unchecked")
    public JSONObject get(Object entity){
        if(entity instanceof Map) return getByMap((Map<Object,Object>)entity);
        else return getByObject(entity);
    }
    
    @SuppressWarnings("unchecked")
    private JSONObject getByObject(Object entity){
        
        String fieldName = null;
        
        try {
            JSONObject json = new JSONObject();
            if(entity==null) return json;
            
            Method[] methods = entity.getClass().getMethods();
            Class returnType = null;
            
            for(Method method : methods){
                
                String name = method.getName();
                
                fieldName = Strings.getFieldName(name);
                if (fieldName==null) continue;
                
                returnType = method.getReturnType();
                
                Mapping annotation =  (Mapping)method.getAnnotation(Mapping.class);
                
                Annotation[] annos = method.getAnnotations();
                
                //애는 걸러준다.
                if(Sets.isInstance(annos,Hidden.class)) continue;

                if(Sets.isSame(returnType,String.class,BigDecimal.class,Long.class,Integer.class,Boolean.class
                        ,boolean.class,int.class,long.class)){
                    Object obj = method.invoke(entity);
                    if(obj!=null) json.put(fieldName,obj);
                }
                
                for(JsonConfig each: list){
                    if(each.run(json, method, returnType, annos)) continue;
                }
                
                if(returnType == Calendar.class){
                    Object obj = method.invoke(entity);
                    if(obj!=null) json.put(fieldName, Days.DATE.get((Calendar)obj));
                }else if(returnType == Date.class){
                  //추후 구현
                }else if(returnType.isEnum()){
                    Enum num = (Enum)method.invoke(entity);
                    if(num!=null){
                        json.put(fieldName+"Name",num.toString());
                        json.put(fieldName,num.name());
                    }
                }
                
                if(annotation != null && annotation.mappingType() == MappingType.ENTITY){
                    Object obj = method.invoke(entity);
                    if(obj!=null) json.put(fieldName,getByObject(obj));
                }else if(annotation != null && annotation.mappingType() == MappingType.LIST_PARTITIONED_STRING){
                    Object obj = method.invoke(entity);
                    //json.put(fieldName, encoder.escapeJavaScript(Formats.getOracleStr((List)obj)));
                    json.put(fieldName, Sets.getOracleStr((List)obj));
                }else if(annotation != null && annotation.mappingType() == MappingType.LIST_SUB_ENTITY){
                    JSONArray jsonArray = new JSONArray();
                    List sublist = (List)method.invoke(entity);
                    for(Object object:sublist){
                        if(object instanceof String) jsonArray.add(object.toString());  //스트링 배열일경우 
                        else jsonArray.add(getByObject(object));
                    }
                    json.put(fieldName, jsonArray);
                }
            }
            return json;
        }
        catch (Exception e) {
            throw new RuntimeException(fieldName+" reflection is fail",e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private JSONObject getByMap(Map<Object,Object> map){
        Object obj ;
        JSONObject json = new JSONObject();
        for(Object key : map.keySet()){
            obj = map.get(key);
            Class<?> clazz = obj.getClass();
            if( Sets.isEquals(STRING_TYPE, clazz)) {
                json.put(key, obj);
            }else if(clazz == String[].class ) {  //request에서 받아올때 주로 사용~
                String[] temp = (String[])obj;
                JSONArray jsonArray = new JSONArray();
                for(int index = 0;index<temp.length;index++){
                    jsonArray.add(temp[index]);
                }
                json.put(key, jsonArray);
            }else if(obj instanceof Date) {
                json.put(key, Days.DATE_SIMPLE.get((Date)obj));
            }else if(clazz== Timestamp.class) { //iBatis 기본 Date유형
                Timestamp t = (Timestamp)obj;
                json.put(key, Days.DATE_SIMPLE.get(new Date(t.getTime()))); //???????? 확인 안해봄.. 몰라.
            }
        }
        return json;
    }

    /**
     * JSON에 자신을 가르키는 this를 obj로 포함시킨다. (obj:this)
     **/
    public String addJsonThis(JSONObject e) {
        if(e==null) return null;
        if(e.size()==0) return e.toString();
        else return "{obj:this,"+e.toString().substring(1);
    }
    
  
    
}