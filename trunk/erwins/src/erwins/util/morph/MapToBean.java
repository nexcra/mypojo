package erwins.util.morph;


import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import erwins.util.lib.Strings;
import erwins.util.root.StaticLogger;


/**
 * map을 Bean으로 변경한다... 거의 사용될일은 없을듯,.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class MapToBean extends StaticLogger{
    
    /**
     * iBatis 등에서 얻어온 resultmap의  mapList를 beanList으로 변환한다.
     * 절대 성능이 필요하지 않은 곳에서만 사용한다.
     **/
    @SuppressWarnings("unchecked")
    public static List<Object> loof(List<HashMap> mapList,Class clazz){
        List<Object> beanList = new ArrayList<Object>();
        
        for(HashMap<String,Object> map : mapList) {
            Object bo;
            try {
                bo = (Object) makeBean(map,clazz);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("runtime fail");
            }
            beanList.add(bo);
        }
        return beanList;
    }
      
    /**
     * HashMap을 bean으로 변환한다.
     **/
    public static Object makeBean(HashMap<String,Object> map,Class<?> entityType) throws Exception{
        
        //Class entityClass = entity.getClass();
        Object entity = (Object)entityType.newInstance();
        Method[] methods = entityType.getMethods();                
        Class<?> setterType = null;
        
        StringBuffer omission = new StringBuffer();
        
        for(Method method:methods ){

            String name = method.getName();
            if (!name.startsWith("set") || name.length() < 4 ) continue;
            
            setterType = method.getParameterTypes()[0]; //bean의 setter의 1번재 parameter를 기준으로 데이터를 검색한다.
            
            String fieldName = Strings.getFieldName(name);
            //Mapping annotation =  (Mapping)method.getAnnotation(Mapping.class);
            
            Object obj = map.get(Strings.getUnderscore(fieldName));
            if(obj == null) continue;
            
            if(setterType == String.class){
                String temp =  (String)obj;
                method.invoke(entity, temp);
            }else if(setterType == int.class || setterType == Integer.class){
                method.invoke(entity, ((BigDecimal)obj).intValue());
            }else if(setterType == long.class || setterType == Long.class){
                method.invoke(entity, ((BigDecimal)obj).longValue());
            }else if(setterType == BigDecimal.class || setterType == Date.class){
                method.invoke(entity, obj);
            }else if(setterType == Calendar.class){
                Calendar car = Calendar.getInstance();
                car.setTime((Date)obj);
                method.invoke(entity, car);
            }else{
                omission.append(Strings.getFieldName(method.getName())+", ");
            }
        }
        if(!omission.toString().equals("")) log.debug("omission : " + omission);;        
        return entity;
    }
    
}