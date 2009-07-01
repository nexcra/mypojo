package erwins.util.mapping;


import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import erwins.util.lib.*;
import erwins.util.morph.Mapping;
import erwins.util.morph.Mapping.MappingType;


/**
 * 자료를 변환한다. (주로 HttpServletRequest or Map or Bean)
 * Bean의 경우 컴포는트 타입을 인식 할 수 있다.  List(User) 타입
 * 자식객체의 경우 name으로 property.~~형식을 사용한다.
 * 키값, Date를 제외한 모든 객체는 null safe하다. 
 * 경력 만 1년차인 2008년 6월 버전 1 제작... 이후 업데이트 중단
 * @author erwins(my.pojo@gmail.com)
 */
public class Rq extends GetValuePolicy{
    
    private static Logger log = Logger.getLogger(Rq.class);
     
    /**
     * 생성시 debug log를 남긴다.
     */
    public Rq(HttpServletRequest request){
        
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
     * @throws Exception 
     * @throws InstantiationException 
     */
    @SuppressWarnings("unchecked")    
    public <T> T getBean(Class<T> entityType){
        
        try {
            T entity = (T)entityType.newInstance();
            Method[] methods = entityType.getMethods();                
            Class setterType = null;
            
            StringBuffer omission = new StringBuffer();
            
            for(Method method:methods ){
                String name = method.getName();
                if (!name.startsWith("set") || name.length() < 4 ) continue;
                
                setterType = method.getParameterTypes()[0]; //bean의 setter의 1번재 parameter를 기준으로 데이터를 검색한다.            
                
                String fieldName = Strings.getFieldName(name);            
                Mapping annotation =  (Mapping)method.getAnnotation(Mapping.class);
                MappingType type = null;
                if(annotation!=null) type = annotation.mappingType();
                
                Id id =  (Id)method.getAnnotation(Id.class);
                
                if(setterType == String.class){
                    method.invoke(entity, getStr(fieldName));
                }else if(setterType == Integer.class || setterType == int.class){
                    if(id!=null || type == MappingType.FK) method.invoke(entity, getIntId(fieldName)); 
                    else method.invoke(entity, getInteger(fieldName));
                }else if(setterType == BigDecimal.class){
                    method.invoke(entity, getDeciaml(fieldName));
                }else if(setterType == boolean.class){             
                    Boolean boo = getBoolean(fieldName); 
                    if(boo == null) continue; //yn 이외의 이상한 값이면 디폴트 값을 사용
                    method.invoke(entity, boo);
                }else if(setterType.isEnum()){
                    //임시조치
                    if(method.getAnnotation(Enumerated.class)!=null)
                        method.invoke(entity, Enum.valueOf(setterType, getStr(fieldName)));
                }else if(setterType == Long.class || setterType == long.class){
                    if(id!=null || type == MappingType.FK) method.invoke(entity, getLongId(fieldName));
                    else method.invoke(entity, getLong(fieldName));
                }else if(annotation != null && annotation.mappingType() == MappingType.LIST_PARTITIONED_STRING){ 
                    String str = getStr(fieldName);
                    method.invoke(entity, Sets.getOracleStr(str));
                }else if(annotation != null && annotation.mappingType() == MappingType.LIST_SUB_ENTITY){                
                    /**
                     * 서브클래스의 입력작업을 시작한다.
                     * 추후 재귀참조로 변형하자.
                     */
                    Class subEntityClass = Clazz.getSetterGeneric(method);      
                    
                    List subEntitylist = null;
                    
                    Method[] subMethods = subEntityClass.getMethods();
                    
                    Class subClassType = null;
                    
                    for(Method subMethod : subMethods){
                        
                        String subName = subMethod.getName();
                        if (!subName.startsWith("set") || subName.length() < 4 ) continue;
                        
                        subClassType = subMethod.getParameterTypes()[0];
                        
                        Mapping subAnnotation =  (Mapping)subMethod.getAnnotation(Mapping.class);
                        MappingType subType = null;
                        if(subAnnotation!=null) subType = subAnnotation.mappingType();

                        //String subFieldName = (isMultiEntity) ?  entityType.getSimpleName()+ "."+ Formats.getFieldName(subName) : Formats.getFieldName(subName);
                        String subFieldName = Strings.getFieldName(subName);
                        Object[] temp = null;
                        //subFieldName~~하기 작성후 테스트 하기.. 

                        if(subClassType == String.class){
                            temp = getStrs(subFieldName);                                           
                        }else if(subClassType == Boolean.class || subClassType == boolean.class){
                            temp = getBooleans(subFieldName);
                        }else if(subClassType == int.class || subClassType == Integer.class){
                            if(subType == MappingType.FK) temp = getIntIds(subFieldName); 
                            else temp = getIntegers(subFieldName);
                        }else if(subClassType == long.class || subClassType == Long.class){
                            if(subType == MappingType.FK) temp = getLongIds(subFieldName);
                            else temp =  getLongs(subFieldName);
                        }else if(subClassType == BigDecimal.class){
                            temp = getDeciamls(subFieldName);
                        }else{
                            omission.append(Strings.getFieldName(subMethod.getName())+", ");
                            continue;
                        }
                        
                        subEntitylist = Clazz.initCollection(subEntitylist,subEntityClass,temp.length);
                        addArrayToList(subMethod,subEntitylist,temp);
                        
                    }
                    if(subEntitylist==null) continue; //만약 인자가 하나도 들어오지 않았다면 초기값을 건드리지 않고 무시한다.
                    method.invoke(entity, subEntitylist);                    
                    
                }else{
                    omission.append(Strings.getFieldName(method.getName())+", ");
                }
            }
            log.debug("omission : " + omission);
            return entity;
        }
        catch (Exception e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    /**
     * 배열을 순회하면서 List에 저장한다.
     */
    private void addArrayToList(Method subMethod,List subEntitylist,Object[] temp) throws Exception{
        for(int y=0;y<temp.length;y++){
            Object subBean = subEntitylist.get(y);
            Object value = temp[y];
            if(value==null) continue;  //Boolean의 특이한 케이스 때문에 넣었다. 해당 없으면 디폴트 boolean을 사용한다.
            subMethod.invoke(subBean,value);                                
        }
    }
 
    
}