package erwins.util.morph;


import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.CollectionOfElements;

import erwins.util.exception.MalformedException;
import erwins.util.lib.Clazz;
import erwins.util.lib.Sets;
import erwins.util.lib.Strings;
import erwins.util.morph.anno.Fk;
import erwins.util.morph.anno.Numeric;
import erwins.util.morph.anno.OracleListString;
import erwins.util.root.EntityId;
import erwins.util.root.Singleton;
import erwins.util.tools.Mapp;
import erwins.util.valueObject.ValueObject;

/**
 * RequestResolver.. ㅠㅠ
 * 자료를 변환한다. (주로 HttpServletRequest or Map or Bean)
 * Bean의 경우 컴포는트 타입을 인식 할 수 있다.  List(User) 타입
 * 자식객체의 경우 name으로 property.~~형식을 사용한다.
 * 키값, Date를 제외한 모든 객체는 null safe하다. 
 * !!! 사용자 정의 Code를 자동으로 받게 변경
 * @author erwins(my.pojo@gmail.com)
 */
@Singleton
@SuppressWarnings("unchecked")
public class Dissolver{
    
    private static Dissolver theInstance = new Dissolver();
    
    public static Dissolver instance(){
        return theInstance;
    }
    
    private List<DissolverConfig> list = new CopyOnWriteArrayList<DissolverConfig>();

    public Dissolver add(DissolverConfig command) {
        list.add(command);
        return this;
    }

    /** 결과로 Object를 반환한다. null이면 부합되는 조건이 아닐 경우이다. */
    public interface DissolverConfig {
        public Object run(String value,Class<?> setterType, Annotation[] annos);
    }
    
    // ===========================================================================================
    //                               protected      
    // ===========================================================================================    
    
    /**
     * ID이거나 Mapping의 FK가 붙어있다면 nullSafe를 적용하지 않는다.
     */
    public <T> T getBean(HttpServletRequest request,Class<T> clazz){
        Mapp map = new Mapp(request);
        return new BeanDissolver<T>(map).get(clazz);
    }
    public <T> T getBean(Mapp map,Class<T> clazz){
    	return new BeanDissolver<T>(map).get(clazz);
    }
    public <T> T getBean(Map<?,?> map,Class<T> clazz){
    	Mapp mapp = new Mapp();
    	mapp.putAll(map);
    	return new BeanDissolver<T>(mapp).get(clazz);
    }
    
    private class BeanDissolver<T>{
        private Method[] methods;
        private Class<?> setterType;
        private String fieldName;
        private Annotation[] annos;
        private Mapp map;
        private Class<?> clazz;
        
        /** 어노테이션을 확인할때 사용된다. */
        private Method getter;
        
        private BeanDissolver(Mapp map) {
            this.map = map;
        }
        
        public T get(Class<T> clazz){
            try {
                return getBean(clazz);
            }
            catch (Exception e){
                throw new RuntimeException("Exception by [Request to Class]",e);
            }
        }
        
        private T getBean(Class<T> clazz) throws Exception{
        	this.clazz = clazz;
            T entity = clazz.newInstance();
            methods = entity.getClass().getMethods();
            
            for(Method method:methods ){
            	getter = null;
                if(!initSetter(method)) continue;
                
                //콘피그 설정을 먼저 검사한다.
                Object result = null;
                for (DissolverConfig each : list) {
                    String value = map.getStr(fieldName);
                    if(Strings.isEmpty(value)) continue;
                    result = each.run(value, setterType, annos);
                    if(result!=null){
                        method.invoke(entity, result);
                        break;
                    }
                }
                if(result!=null) continue;
                
                //getMethods()는 슈퍼클래스의 오버라이드 메소드역시 가져온다. 이는 무시해주자.
                if(method.isBridge()) continue;
                
                if(setterType == String.class){
                    String value = null;
                    if(Sets.isAnnotationPresent(method,Numeric.class)) value = map.getNumericStr(fieldName);
                    else value =  map.getStr(fieldName);
                    method.invoke(entity, value);
                }else if(setterType == Integer.class || setterType == int.class){
                    if(isKey()) method.invoke(entity, map.getIntId(fieldName)); 
                    else method.invoke(entity, map.getInteger(fieldName));
                }else if(setterType == BigDecimal.class){
                    method.invoke(entity, map.getDecimal(fieldName));
                }else if(setterType == boolean.class || setterType == Boolean.class){
                    Boolean boo = map.getBoolean(fieldName); 
                    if(boo == null) continue; //yn 이외의 이상한 값이면 디폴트 값을 사용
                    method.invoke(entity, boo);
                }else if(ValueObject.class.isAssignableFrom(setterType)){ //아직 테스트 못해봄
                	Object obj =  map.get(fieldName);
                	if(Clazz.isEmpty(obj)) continue;
                	ValueObject valueObject = (ValueObject)Clazz.instance(setterType);
                	valueObject.initValue(obj);
                	method.invoke(entity, valueObject);
                }else if(Date.class.isAssignableFrom(setterType)){ //Date의 경우 일단 Long형태만 지원한다.
                	Long value = null;
					try {
						value = map.getLongId(fieldName);
					} catch (Exception e) { //non
					}
                	if(value!=null) method.invoke(entity,new Date(value));
                }else if(setterType.isEnum()){
                    method.invoke(entity,map.getEnum((Class<Enum>)setterType, fieldName)); //기본 Enum만 됨.
                }else if(setterType == Long.class || setterType == long.class){
                    if(isKey()) method.invoke(entity, map.getLongId(fieldName));
                    else method.invoke(entity, map.getLong(fieldName));
                }else{
                	initGetter();
                	if(Sets.isAnnotationPresent(method,OracleListString.class)){ 
                        String str = map.getStr(fieldName);
                        method.invoke(entity, Sets.getOracleStr(str));
                    }else if(Sets.isAnnotationPresent(getter,ManyToOne.class)){
                        String entityName = fieldName + "." + EntityId.ID_NAME;
                        Serializable temp = null;
                        Class<?> idClass = Clazz.getterReturnClass(setterType,EntityId.ID_NAME);
                        if(idClass==Integer.class) temp = map.getIntId(entityName);
                        else if(idClass==Long.class) temp = map.getLongId(entityName);
                        else if(idClass==String.class) temp = (Serializable)map.get(entityName);
                        if(temp==null) continue;
                        EntityId newEntity = (EntityId)Clazz.instance(setterType);
                        newEntity.setId(temp);
                        method.invoke(entity, newEntity);
                    }else if(Sets.isAnnotationPresent(getter,ManyToMany.class)){
                        /** List만 사용되는것이 아니라 Set이 사용될 수도 있다. */
                        Class<?> subEntityClass =  Clazz.getSetterGeneric(method);
                        if(!EntityId.class.isAssignableFrom(subEntityClass)) continue;
                        String collectionName = fieldName + "." + EntityId.ID_NAME;
                        
                        Number[] temp = null;
                        Class<?> idClass = Clazz.getterReturnClass(subEntityClass,EntityId.ID_NAME);
                        if(idClass==Integer.class) temp = map.getIntIds(collectionName);
                        else if(idClass==Long.class) temp = map.getLongIds(collectionName);
                        else throw new RuntimeException(idClass +" is not required! ^^;");
                        
                        Collection<EntityId> subEntitylist = null;
                        if(List.class.isAssignableFrom(setterType)) subEntitylist = new ArrayList<EntityId>();
                        else if(Set.class.isAssignableFrom(setterType)) subEntitylist = new TreeSet<EntityId>();
                        else throw new RuntimeException(setterType +" is not required! ^^;");
                        
                        for(Number each : temp){
                            EntityId idEntity = (EntityId)subEntityClass.newInstance();
                            idEntity.setId(each);
                            subEntitylist.add(idEntity);
                        }
                        method.invoke(entity, subEntitylist);
                    }else if(Sets.isAnnotationPresent(getter,OneToMany.class,CollectionOfElements.class)){ //List만 된다. 주의!
                        
                        String preFix = fieldName;
                        Class<?> subEntityClass =  Clazz.getSetterGeneric(method);
                        
                        //enum일 경우 부가옵션.
                        if(Sets.isAnnotationPresent(getter,Enumerated.class)){
                        	Collection<Enum> c;
                        	if(Set.class.isAssignableFrom(setterType)) c = new HashSet();
                        	else if(Set.class.isAssignableFrom(setterType)) c = new ArrayList();
                        	else throw new MalformedException(setterType+ "is not required class");
                        	
                        	//이하 필요을 모르겠다... 흠냐.. 나중에 수정하자.
                        	Enumerated enumerated = Sets.getInstance(annos, Enumerated.class);
                        	if(enumerated.value().equals(EnumType.STRING)){
                        		String fieldName =  preFix + ".string";
                            	String[] values = map.getStrs(fieldName);
                            	for(String eachEnum : values) c.add(Clazz.getEnum((Class<Enum>)subEntityClass, eachEnum));
                        	}else throw new RuntimeException("sorry. not supported args (EnumType)~ ^^;");
                        	method.invoke(entity, c);
                        }
                        
                        List<?> subEntitylist = null;
                        
                        Method[] subMethods = subEntityClass.getMethods();
                        
                        for(Method subMethod : subMethods){
                            
                            if(!initSetter(subMethod)) continue;
                            
                            fieldName = preFix + "." + fieldName; //preFix설정.
                            
                            if(StringUtils.isEmpty(fieldName)) continue;
                            
                            Object[] temp = null;

                            if(setterType == String.class){
                                temp = map.getStrs(fieldName);                                           
                            }else if(setterType == Boolean.class || setterType == boolean.class){
                                temp = map.getBooleans(fieldName);
                            }else if(setterType == int.class || setterType == Integer.class){
                                if(isKey()) temp = map.getIntIds(fieldName);
                                else temp = map.getIntegers(fieldName);
                            }else if(setterType == long.class || setterType == Long.class){
                                if(isKey()) temp = map.getLongIds(fieldName);
                                else temp =  map.getLongs(fieldName);
                            }else if(setterType == BigDecimal.class){
                                temp = map.getDecimals(fieldName);
                            }else{
                                continue;
                            }
                            subEntitylist = Clazz.initCollection(subEntitylist,subEntityClass,temp.length);
                            addArrayToList(subMethod,subEntitylist,temp);
                        }
                        if(subEntitylist==null) continue; //만약 인자가 하나도 들어오지 않았다면 초기값을 건드리지 않고 무시한다.
                        method.invoke(entity, subEntitylist);
                    }
                }
            }
            return entity;
        }

        /** 이후는 어노테이션으로 구해야 함으로 어노테이션이 붙는 getter를 찾아준다. */
		private void initGetter() {
			if(getter!=null) return;
			getter = Clazz.toGetter(clazz,fieldName);
		}

        /**
         * method의 정보를 추출한다. 해당 조건이 아니면 false를 리턴한다..
         * 일반적으로 getter에만 어노테이션을 붙임으로 getter를 참조한다. 
         */
        private boolean initSetter(Method method) {
            String methodName = method.getName();
            fieldName = Strings.setterName(methodName);
            if (fieldName==null) return false;
            if(method.getParameterTypes().length!=1) return false;  //1개의 입력인자만을 인정한다.
            setterType = method.getParameterTypes()[0]; //bean의 setter의 1번재 parameter를 기준으로 데이터를 검색한다.                
            annos = method.getAnnotations();
            
            if(Sets.isEmpty(annos)){ //setter에 annotation이 없을 경우 getter의 annotation을 가죠온다.
            	Method getter = Clazz.toGetter(clazz, fieldName);
            	if(getter!=null) annos = getter.getAnnotations();
            }
            
            return true;
        }
        
        /**
         * Id가 붙어있거나 Fk가 붙어있을경우 true를 리턴
         */
        private boolean isKey(){
        	initGetter();
            return Sets.isAnnotationPresent(getter,Id.class,Fk.class); 
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
    
    
}