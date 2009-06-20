package erwins.util.lib;


import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * 리플렉션 관련 Util
 * 특성상 특이하게.. 예외는 모두 던진다.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class Clazz {
    
    /**
     * 빈 객체인가?
     */
    public static boolean isEmpty(Object obj){
        if(obj==null) return true;
        if(obj instanceof String)
            return Strings.isEmpty((String)obj);
        else if(obj instanceof Collection)
            return Sets.isEmpty((Collection<?>)obj);
        else if(obj instanceof Map)
            return ((Map<?,?>) obj).isEmpty();            
        else if(obj instanceof BigDecimal)
            return ((BigDecimal) obj).equals(BigDecimal.ZERO);            
        else return Strings.isEmpty(obj.toString());
    }
    
    /** 
     *  현재 GenericInterface의 T를 1개만 추출한다.
     *  ex) (Class<?>)getFirstGeneric(GenericHibernateDao.class);
     **/
    public static Class<?> getFirstGeneric(Class<?> clazz) {
        return (Class<?>)((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    /** 
     *  구현한 GenericInterface의 T를 1개만 추출한다.
     *  ex) extractTypeParameter(BaseEntity.class);
     *  없으면 null을 리턴한다.
     **/
    public static Class<?> extractTypeParameter(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        
        ParameterizedType genericInterface = null;
        for (Type t : genericInterfaces) {
            if (t instanceof ParameterizedType) {
                genericInterface = (ParameterizedType)t;
                break;
            }
        }
        
        if(genericInterface == null)  return null;
        
        return (Class<?>)genericInterface.getActualTypeArguments()[0];
    }   
    
    /**
     * 제너릭 1번째 인자의 instance를 생성한다.
     * 검증완료
     */
    @SuppressWarnings("unchecked")
    public static <T> T genericInstance(Class<?> clazz) {
        ParameterizedType c = (ParameterizedType)clazz.getGenericSuperclass();
        Class<?> cx = (Class<?>)c.getActualTypeArguments()[0];
        try {
            return (T)cx.newInstance();
        }
        catch (Exception e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T extractInstance(Class<?> clazz) {
        try {
            return (T)extractTypeParameter(clazz).newInstance();
        }
        catch (InstantiationException e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
        catch (IllegalAccessException e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Enum> T getEnum(Class<?> clazz,String name){
        if(!clazz.isEnum()) throw new RuntimeException(clazz + " is not Enum");
        return (T) Enum.valueOf((Class<Enum>)clazz, name);
    }

    /**
     * name에 해당하는 Enum들을 모두 반환한다. 
     * Flex등의 요청으로 json등을 만들때 사용한다.
     */
    @SuppressWarnings("unchecked")
    public static Enum<?>[] getEnums(String fullName){
        Class<Enum<?>> en = null;
        try {
            en = (Class<Enum<?>>) Class.forName(fullName);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        return en.getEnumConstants();
    }
    
    /**
     * 최초 1회만 실행. 
     * List에 최초의 requestParameters(size)만큼 자식 객체 생성
     */
    @SuppressWarnings("unchecked")
    public static List initCollection(List subBeanlist,Class subBeanClass,int size){
        if(size==0 || subBeanlist!=null) return subBeanlist;
        else{
            subBeanlist = new ArrayList();
            for(int x=0;x<size;x++){
                Object subBean;
                try {
                    subBean = subBeanClass.newInstance();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("runtime fail");
                }
                subBeanlist.add(subBean);
            }
            return subBeanlist;
        }
    }
    
    public static <T> List<T> initCollection2(List<T> subBeanlist,Class<T> subBeanClass,int size){
        //subBeanlist.getClass().
        if(size==0 || subBeanlist!=null) return subBeanlist;
        else{
            subBeanlist = new ArrayList<T>();
            for(int x=0;x<size;x++){
                T subBean;
                try {
                    subBean = subBeanClass.newInstance();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("runtime fail");
                }
                subBeanlist.add(subBean);
            }
            return subBeanlist;
        }
    }     
    
    /**
     * Setter Method의 Collection제너릭 타입 이름을 추출한다.
     * Class에사 제너릭이 붙은 method는 오직 setter 뿐이다.
     */
    public static Class<?> getSetterGeneric(Method method) throws ClassNotFoundException{
        String subClassName = method.getGenericParameterTypes()[0].toString();
        subClassName = subClassName.substring(subClassName.indexOf("<"));
        subClassName = subClassName.replaceAll("<","").replaceAll(">","");
        return Class.forName(subClassName);
    }
    
    /**
     * Transient 어노테이션이 붙어있는지 검사.
     **/
    public static boolean isTransient(Method method){
        if(method.getAnnotation(Transient.class)==null) return false;
        else return true;
    }    
    
             
    /**
     * clazz의 fieldName에 적합하게 value를 캐스팅해서 리턴한다.
     * 단순 String타입의 value를 적절해 캐스팅한 ,Object로 보내줘야 하는 HibernateCriterion에서 사용한다.
     * enum의 경우 특수하게 표현
     */
    @SuppressWarnings("unchecked")
    public static Object getCastedValue(Class<?> clazz,String fieldName,String value) {
        Class<?> field;
        try {
            field = clazz.getDeclaredField(fieldName).getType();
        }
        catch (NoSuchFieldException e) {
            Encoders.stackTrace(e);
            throw new RuntimeException(e.getMessage(),e);
        }
        if(field.isEnum()){
            return Enum.valueOf((Class)field, value);
        }else
            return field.cast(value);
    }
    
    /**
     * Class의 패키지 이름을 가져온다.
     */
    public static String getPackageName(Class<?> clazz) {
        if (clazz == null) return StringUtils.EMPTY;
        String classFullName = clazz.getCanonicalName();
        return classFullName.substring(0,classFullName.lastIndexOf("."));
    }

    /**
     * getter를 사용하여  객체를 반환한다.
     * methodName or FieldName을 넣자.
     */
    public static Object getObject(Object obj,String name){
        if(!name.startsWith("get")) name = "get" + WordUtils.capitalize(name); 
        try {
            return obj.getClass().getMethod(name).invoke(obj);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("runtime fail");
        }

    }    
    
    /**
     * 인스턴스의 필드중에서 해당하는 이름의 필드에 그 값을 할당한다.
     */
    public static void setObject(Object instance, String fieldName,Object value){
        Class<?> clazz = instance.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            // field null 체크할 필요가 없음(field가 null이면 NoSuchFieldException이 발생)
            field.set(instance, value);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("runtime fail");
        }
    }

    /**
     * methodName(setter/geter/is)에서 fildName을 추출한다.
     * 해당 조건이 아니면 null을 리턴한다.
     */
    public static String getFieldName(Method method) {
        String name = method.getName();
        return Strings.getFieldName(name);
    }
    
    
}