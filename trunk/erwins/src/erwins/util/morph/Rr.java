package erwins.util.morph;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import erwins.util.lib.*;
import erwins.util.morph.Mapping.MappingType;

/**
 * RequestResolver.. ㅠㅠ
 * 자료를 변환한다. (주로 HttpServletRequest or Map or Bean)
 * Bean의 경우 컴포는트 타입을 인식 할 수 있다.  List(User) 타입
 * 자식객체의 경우 name으로 property.~~형식을 사용한다.
 * 키값, Date를 제외한 모든 객체는 null safe하다. 
 * !!! 사용자 정의 Code를 자동으로 받게 변경
 * @author erwins(my.pojo@gmail.com)
 */
public class Rr extends NullPolicy{
    
    private Logger log = Logger.getLogger(this.getClass());
     
    /**
     * 생성시 debug log를 남긴다.
     */
    @SuppressWarnings("unchecked")
    public Rr(HttpServletRequest request){
        
        this.request = request;
        
        if(log.isDebugEnabled()){
            Enumeration<String> parameterNames =  request.getParameterNames();
            List<String> empty = new ArrayList<String>();
            JSONObject parameter = new JSONObject();            
            JSONObject parameters = new JSONObject();
            
            while(parameterNames.hasMoreElements()){
                String name = parameterNames.nextElement();
                String[] values = request.getParameterValues(name);
                //if(values!=null) continue;                
                if(values.length==1){
                    if(values[0].equals("")) empty.add(name);                       
                    else parameter.put(name, values[0]);                     
                }else{
                    JSONArray array = new JSONArray();
                    for(int i=0;i<values.length;i++) array.add(i,values[i]);
                    parameters.put(name, array);
                }
            }
            
            log.debug("======== HTML Parameter ============");
            log.debug("== NAMED.. BUT EMPTY : " + Strings.joinTemp(empty,","));
            if(parameter.keys().hasNext()){
                log.debug("== SINGLE PARAMETER : " + parameter);
            }
            if(parameters.keys().hasNext()){
                log.debug("== TWO OR MORE PARAMETERS : " + parameters);
            }
        }
    }
    
    
    // ===========================================================================================
    //                               protected      
    // ===========================================================================================    

    /** 흠.. 애는 제너릭이 필요없긴 한디..  */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getMap(){
        
        HashMap<String, Object> map = new HashMap<String,Object>();
        
        Enumeration enumeration = request.getParameterNames();
        String name = null;
        String[] values = null;
        
        while (enumeration.hasMoreElements()) {
            name = (String) enumeration.nextElement();            
            values = getStrs(name);
            if (values.length == 1) map.put(name, values[0]);
            else  map.put(name, values);
        }
        return map;
    }

    
    /**
     * ID이거나 Mapping의 FK가 붙어있다면 nullSafe를 적용하지 않는다.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getBean(Class<T> clazz){
        //Class<?> clazz = Clazz.extractTypeParameter(this.getClass());
        return (T)new Resolver().get(clazz);
    }
    
    private class Resolver{
        private Method[] methods;
        private Class<?> setterType;
        private String fieldName;
        private Annotation[] annos;
        
        Resolver() {}
        
        public Object get(Class<?> clazz){
            try {
                return getBean(clazz);
            }
            catch (Exception e){
                throw new RuntimeException("runtime fail",e);
            }
        }
        
        private Object getBean(Class<?> clazz) throws Exception{
            Object entity = clazz.newInstance();
            methods = entity.getClass().getMethods();
            
            for(Method method:methods ){
                
                if(!initSetter(method)) continue;
                
                if(setterType == String.class){
                    method.invoke(entity, getStr(fieldName));
                }else if(setterType == Integer.class || setterType == int.class){
                    if(isKey()) method.invoke(entity, getIntId(fieldName)); 
                    else method.invoke(entity, getInteger(fieldName));
                }else if(setterType == BigDecimal.class){
                    method.invoke(entity, getDeciaml(fieldName));
                }else if(setterType == boolean.class){
                    Boolean boo = getBoolean(fieldName); 
                    if(boo == null) continue; //yn 이외의 이상한 값이면 디폴트 값을 사용
                    method.invoke(entity, boo);
                }else if(setterType.isEnum()){
                    method.invoke(entity,Clazz.getEnum((Class<Enum>)setterType, fieldName));                    
                }else if(setterType == Long.class || setterType == long.class){
                    if(isKey()) method.invoke(entity, getLongId(fieldName));
                    else method.invoke(entity, getLong(fieldName));
                }else if(MappingType.LIST_PARTITIONED_STRING.isMatch(annos)){ 
                    String str = getStr(fieldName);
                    method.invoke(entity, Sets.getOracleStr(str));
                }else if(MappingType.LIST_SUB_ENTITY.isMatch(annos)){
                    
                    /**
                     * 서브클래스의 입력작업을 시작한다.
                     * 추후 재귀참조로 변형하자.
                     */
                    
                    setCollectionName(fieldName);
                    
                    Class<?> subEntityClass =  Clazz.getSetterGeneric(method);
                    List<?> subEntitylist = null;
                    
                    Method[] subMethods = subEntityClass.getMethods();
                    
                    
                    for(Method subMethod : subMethods){
                        
                        if(!initSetter(subMethod)) continue;
                        
                        if(!initSetter(method)) continue;  //??
                        
                        if(StringUtils.isEmpty(fieldName)) continue;
                        
                        Object[] temp = null;

                        if(setterType == String.class){
                            temp = getStrs(fieldName);                                           
                        }else if(setterType == Boolean.class || setterType == boolean.class){
                            temp = getBooleans(fieldName);
                        }else if(setterType == int.class || setterType == Integer.class){
                            if(isKey()) temp = getIntIds(fieldName);
                            else temp = getIntegers(fieldName);
                        }else if(setterType == long.class || setterType == Long.class){
                            if(isKey()) temp = getLongIds(fieldName);
                            else temp =  getLongs(fieldName);
                        }else if(setterType == BigDecimal.class){
                            temp = getDeciamls(fieldName);
                        }else{
                            continue;
                        }
                        subEntitylist = Clazz.initCollection(subEntitylist,subEntityClass,temp.length);
                        addArrayToList(subMethod,subEntitylist,temp);
                        
                    }
                    if(subEntitylist==null) continue; //만약 인자가 하나도 들어오지 않았다면 초기값을 건드리지 않고 무시한다.
                    method.invoke(entity, subEntitylist);   
                    
                    setCollectionName(null);
                }
            }
            return entity;
        }

        /**
         * method의 정보를 추출한다.
         * 해당 조건이 아니면 pass한다. 
         */
        private boolean initSetter(Method method) {
            fieldName = Clazz.getFieldName(method);
            if (StringUtils.isEmpty(fieldName)) return false;
            setterType = method.getParameterTypes()[0]; //bean의 setter의 1번재 parameter를 기준으로 데이터를 검색한다.                
            annos = method.getAnnotations();
            return true;
        }
        
        /**
         * Id가 붙어있거나 Fk가 붙어있을경우 true를 리턴
         */
        private boolean isKey(){
            if(Sets.isEquals(annos, Id.class)) return true;
            if(MappingType.FK.isMatch(annos)) return true;
            return false; 
        }
    }
    
    /**
     * 배열을 순회하면서 List에 저장한다. 
     */
    private void addArrayToList(Method subMethod,List<?> subEntitylist,Object[] temp) throws Exception{
        for(int y=0;y<temp.length;y++){
            Object subBean = subEntitylist.get(y);
            Object value = temp[y];
            if(value==null) continue;  //Boolean의 특이한 케이스 때문에 넣었다. 해당 없으면 디폴트 boolean을 사용한다.
            subMethod.invoke(subBean,value);
        }
    }
    
}