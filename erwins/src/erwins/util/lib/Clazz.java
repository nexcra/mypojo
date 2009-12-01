package erwins.util.lib;


import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * 리플렉션 관련 Util
 * 특성상 특이하게.. 예외는 모두 던진다.
 * <p>Generic정보는 객체에서는 사라지지만 Class에는 남아있어 런타임에 사용 가능하다.</p>
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class Clazz {
    
    /** $$가 있으면 프록시~ */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    
    /**
     * 프록시인지?
     */
    public static boolean isCglibProxy(Object object) {
        if(object == null) return false;
        return isCglibProxyClass(object.getClass());
        //return (object instanceof SpringProxy && isCglibProxyClass(object.getClass()));
    }

    /**
     * CGLIB로 생성된 프록시인지 확인한다.
     * 로딩이 안된 프록시는 JSON등에서 제외할때 등에 제외할깨 사용된다.
     */
    public static boolean isCglibProxyClass(Class<?> clazz) {
        return (clazz != null && clazz.getName().indexOf(CGLIB_CLASS_SEPARATOR) != -1);
    }
    
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
     * generic class에 어떤 클래스가 매핑되었는지 리턴한다.
     * ex) BookDao extends GenericHibernateDao<Board,Integer>
     *   ==> genericClass(BookDao.class,1) : Integer 가 리턴됨.
     */
    public static Class<?> genericClass(Class<?> clazz,int index) {
        ParameterizedType genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
        return (Class<?>)genericSuperclass.getActualTypeArguments()[index];
    }
    
    
    /** name에 해당하는 Enum의 인스턴스를 리턴한다. 커스텀 타입은 사용불가. */
    @SuppressWarnings("unchecked")
    public static <T extends Enum> T getEnum(Class<T> clazz,String name){
        if(!clazz.isEnum()) throw new RuntimeException(clazz + " is not Enum");
        return (T) Enum.valueOf((Class<Enum>)clazz, name);
    }

    /**
     * name에 해당하는 Enum들을 모두 반환한다. 
     * Flex등의 요청(문자열)으로 json등을 만들때 사용한다.
     */
    @SuppressWarnings("unchecked")
    public static Enum<?>[] getEnums(String fullName){
        Class<Enum<?>> en = null;
        try {
            en = (Class<Enum<?>>) Class.forName(fullName);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return en.getEnumConstants();
    }
    
    
    
    /**
     * 최초 1회만 실행.  List에 최초의 requestParameters(size)만큼 자식 객체 생성
     * Dissolver에서 리플렉션시 사용된다.
     */
    @SuppressWarnings("unchecked")
    public static List initCollection(List subBeanlist,Class subBeanClass,int size){
        if(size==0 || subBeanlist!=null) return subBeanlist;
        subBeanlist = new ArrayList();
        for(int x=0;x<size;x++){
            Object subBean;
            try {
                subBean = subBeanClass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            subBeanlist.add(subBean);
        }
        return subBeanlist;
    }
    
    /** reflection에서는 사용 불가. */
    public static <T> List<T> initCollection2(List<T> subBeanlist,Class<T> subBeanClass,int size){
        if(size==0 || subBeanlist!=null) return subBeanlist;
        subBeanlist = new ArrayList<T>();
        for(int x=0;x<size;x++){
            T subBean;
            try {
                subBean = subBeanClass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            subBeanlist.add(subBean);
        }
        return subBeanlist;
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
     * clazz의 fieldName에 적합하게 value를 캐스팅해서 리턴한다.
     * 단순 String타입의 value를 적절해 캐스팅한 ,Object로 보내줘야 하는 HibernateCriterion에서 사용한다.
     * enum의 경우 특수하게 표현
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCastedValue(Class<?> clazz,String fieldName,String value) {
        Class<T> field;
        try {
            field = (Class<T>)clazz.getDeclaredField(fieldName).getType();
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        if(field.isEnum()) return (T)Enum.valueOf((Class)field, value);
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
    
    /** Class의 파일을 가져온다. Groovy 디버깅 용이다. */
    public static File sourceFile(Class<?> clazz) {
        final String src =  "src/";
        String name = clazz.getName().replaceAll("\\.","/");
        File file = new File(src+name + ".java");
        if(file.exists()) return file;
        file = new File(src+name + ".groovy");
        if(file.exists()) return file;
        throw new IllegalArgumentException(clazz + " not fount");
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
            throw new RuntimeException(e);
        }
    }
    
    /** getter의 name에 해당하는 리턴class를 리턴한다. */
    public static Class<?> getterReturnClass(Class<?> clazz,String name){
        if(!name.startsWith("get")) name = "get" + WordUtils.capitalize(name);
        try {
            return clazz.getMethod(name).getReturnType();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
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
            throw new RuntimeException(e);
        }
    }
    
    /**
     * getter인가?
     */
    public static boolean isGetter(Method method) {
        String name = method.getName();
        return Strings.getFieldName(name)==null ? false :true;
    }
    
    
    /*
     * 

    public static Object genericInstance(Class<?> clazz) {
        Class<?> cx = genericClass(clazz,0);
        try {
            return cx.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T extractInstance(Class<T> clazz) {
        try {
            return (T)extractTypeParameter(clazz).newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
 
     /** 
     *  구현한 GenericInterface의 T를 1개만 추출한다.
     *  ex) extractTypeParameter(BaseEntity.class);
     *  없으면 null을 리턴한다.
     **/
    /*
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
 
 
     */
    
    
}