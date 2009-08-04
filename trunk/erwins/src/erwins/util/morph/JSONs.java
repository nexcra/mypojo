package erwins.util.morph;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import javax.persistence.ManyToOne;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Hibernate;

import erwins.util.lib.*;
import erwins.util.morph.Mapping.MappingType;
import erwins.util.morph.anno.Hidden;
import erwins.util.root.EntityId;


/**
 * 수정 금지.
 * @author erwins(my.pojo@gmail.com)
 */
@Deprecated
public abstract class JSONs{
    
    public JSONs(){};
    
    /**
     * Flex등에서 사용한다. list가 0일 경우에는 클라이언트 에서 처리해 주자. 
     */
    public static <T extends Object> JSONArray get(List<T> list){
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
    public static JSONArray get(Enum<?>[] en,boolean isAll, Enum<?> ... ingnor) {
        JSONArray jsonArray = new JSONArray();
        if(isAll) jsonArray.add(emptyJson());
        for (Enum<?> mode : en) {
            if(Sets.isEqualsAny(ingnor, mode)) continue;
            JSONObject json = new JSONObject();
            //code.setIdByInt(mode.ordinal());
            json.put("ordinal", mode.ordinal());
            json.put("label", mode.toString());
            json.put("value", mode.name());  //name할려다가~
            jsonArray.add(json);
        }
        return jsonArray;
    }
    
    private static JSONObject emptyJson(){
        JSONObject json = new JSONObject();
        json.put("label", "전체");
        json.put("data","");
        return json;
    }
    
    /**
     * Map과 Bean 2가지 타입을 지원한다. 
     */
    @SuppressWarnings("unchecked")
    public static JSONObject get(Object entity){
        if(entity instanceof Map) return getByMap((Map<Object,Object>)entity);
        return getByObject(entity);
    }
    
    /**
     * 파라메터가 없는 getter만 가져온다. 
     */
    @SuppressWarnings("unchecked")
    private static JSONObject getByObject(Object entity){
        
        String fieldName = null;
        
        try {
            JSONObject json = new JSONObject();
            if(entity==null) return json;
            
            Method[] methods = entity.getClass().getMethods();
            Class returnType = null;
            
            for(Method method : methods){
                String name = method.getName();
                
                fieldName = Strings.getterName(name);
                if (fieldName==null) continue;
                
                if(method.getParameterTypes().length!=0) continue;
                
                returnType = method.getReturnType();
                
                Mapping annotation =  method.getAnnotation(Mapping.class);
                
                Annotation[] annos = method.getAnnotations();
                
                
                
                //애는 걸러준다.
                if(Sets.isInstanceAny(annos,Hidden.class)) continue;

                if(Sets.isSameAny(returnType,String.class,BigDecimal.class,Long.class,Integer.class,Boolean.class
                        ,boolean.class,int.class,long.class)){
                    Object obj = method.invoke(entity);
                    if(obj!=null) json.put(fieldName,obj);
                }else if(returnType == Calendar.class){
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
                }else if(Sets.isInstanceAny(annos, ManyToOne.class)){
                    Object obj = method.invoke(entity);
                    if(obj==null) continue; 
                    //프록시 객체의 경우 id만을 로딩하자.
                    if(Clazz.isCglibProxy(obj)){
                        if(obj instanceof EntityId){
                            EntityId temp = (EntityId)obj;
                            JSONObject proxy = new JSONObject();
                            proxy.put("id", temp.getId());
                            json.put(fieldName,proxy);    
                            //Flex 게시판 등의 단일 뎁스를 위해준비.
                            json.put(fieldName+"Id",temp.getId());
                        }
                    }else json.put(fieldName,get(obj)); 
                     
                }else if(annotation != null && annotation.mappingType() == MappingType.LIST_PARTITIONED_STRING){
                    Object obj = method.invoke(entity);
                    if(!Hibernate.isInitialized(obj)) continue;
                    json.put(fieldName, Sets.getOracleStr((List)obj));
                }else if(annotation != null && annotation.mappingType() == MappingType.LIST_SUB_ENTITY){
                    JSONArray jsonArray = new JSONArray();
                    List sublist = (List)method.invoke(entity);
                    for(Object object:sublist){
                        if(object instanceof String) jsonArray.add(object.toString());  //스트링 배열일경우 
                        else jsonArray.add(get(object));
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
    
    private static Class<?>[] STRING_TYPE = new Class<?>[]{String.class,BigDecimal.class,int.class,Integer.class,long.class,Long.class};
    
    @SuppressWarnings("unchecked")
    private static JSONObject getByMap(Map<Object,Object> map){
        Object obj ;
        JSONObject json = new JSONObject();
        for(Object key : map.keySet()){
            obj = map.get(key);
            Class<?> clazz = obj.getClass();
            if( Sets.isEqualsAny(STRING_TYPE, clazz)) {
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
    public static String addJsonThis(JSONObject e) {
        if(e==null) return null;
        if(e.size()==0) return e.toString();
        return "{obj:this,"+e.toString().substring(1);
    }
    
}