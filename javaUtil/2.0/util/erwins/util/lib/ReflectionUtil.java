package erwins.util.lib;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Maps;

import erwins.util.collections.MapForList;
import erwins.util.exception.BusinessException;
import erwins.util.root.EntityId;
import erwins.util.root.Pair;

/**
 * 이놈은 예외로 커먼스를 사용하지 않고 spring을 사용한다.
 * spring의 GenericTypeResolver 를 참조할것
 */
public abstract class ReflectionUtil extends ReflectionUtils {

	/** 프록시인지? */
	public static boolean isCglibProxy(Object object) {
		return AopUtils.isCglibProxy(object);
	}

	/** CGLIB로 생성된 프록시인지 확인한다. 로딩이 안된 프록시는 JSON등에서 제외할때 등에 제외할깨 사용된다. */
	public static boolean isCglibProxyClass(Class<?> clazz) {
		return AopUtils.isCglibProxyClass(clazz);
	}

	/** 빈 객체인가? */
	public static boolean isEmpty(Object obj) {
		if (obj == null) return true;
		if (obj instanceof String) return StringUtil.isEmpty((String) obj);
		else if (obj instanceof Collection) return CollectionUtil.isEmpty((Collection<?>) obj);
		else if (obj instanceof Map) return ((Map<?, ?>) obj).isEmpty();
		else if (obj instanceof BigDecimal) return ((BigDecimal) obj).equals(BigDecimal.ZERO);
		else return StringUtil.isEmpty(obj.toString());
	}

	/**
	 * generic class에 어떤 클래스가 매핑되었는지 리턴한다. ex) BookDao extends
	 * GenericHibernateDao<Board,Integer> ==> genericClass(BookDao.class,1) :
	 * Integer 가 리턴됨.
	 */
	/*public static Class<?> genericClass(Class<?> clazz, int index) {
		ParameterizedType genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
		return (Class<?>) genericSuperclass.getActualTypeArguments()[index];
	}*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Class<T> genericClass(Class clazz, int index) {
		ParameterizedType genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
		return  (Class<T>) genericSuperclass.getActualTypeArguments()[index];
	}
	
	
	/** T로 제너릭된 클래스의 T를 인스턴스시켜서 리턴한다. 테스트 필요 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T genericInstance(Class<?> genericClazz) {
		ParameterizedType genericSuperclass = (ParameterizedType) genericClazz.getGenericSuperclass();
		Type type = genericSuperclass.getActualTypeArguments()[0];
		Class<T> classT = null;
		if (type instanceof ParameterizedType) {
			classT = (Class) ((ParameterizedType) type).getRawType();
		} else {
			classT = (Class) type;
		}
		try {
			return classT.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}	

	/** name에 해당하는 Enum의 인스턴스를 리턴한다. 커스텀 타입은 사용불가. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Enum> T getEnumInstance(Class<T> clazz, String name) {
		if (!clazz.isEnum()) throw new RuntimeException(clazz + " is not Enum");
		return (T) Enum.valueOf((Class<Enum>) clazz, name);
	}

	/** Enum인데 Pair인놈의 객체를 가져온다. Pair의 value로 검색 가능하다. 제한적으로 사용하자. */
	public static Pair getEnumPairInstance(Class<Enum<?>> clazz, String value) {
		Enum<?>[] enums = ReflectionUtil.getEnums(clazz);
		for (Enum<?> each : enums) {
			Pair pair = (Pair) each;
			if (pair.getValue().equals(value)) return pair;
		}
		throw new BusinessException("[{0}] is not found from {1}", value, clazz.getSimpleName());
	}
	
	/** getter인거만 추출한다. ??? */
	public static List<Method> getGetterMethod(Class<?> clazz) {
		List<Method> result = new ArrayList<Method>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			String name = method.getName();
			String fieldName = StringUtil.getterName(name);
			if (fieldName == null) continue;
			if (method.getParameterTypes().length != 0) continue;
			/*
			Class<?> returnType = method.getReturnType();
			if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType)
					|| Date.class.isAssignableFrom(returnType) || Number.class.isAssignableFrom(returnType)
					|| ValueObject.class.isAssignableFrom(returnType) || Pair.class.isAssignableFrom(returnType)
					|| Boolean.class.isAssignableFrom(returnType)) result.add(method);
					*/
			result.add(method);
		}
		return result;
	}

	/**
	 * name에 해당하는 Enum들을 모두 반환한다. Flex등의 요청(문자열)으로 json등을 만들때 사용한다.
	 */
	@SuppressWarnings("unchecked")
	public static Enum<?>[] getEnums(String fullName) {
		Class<Enum<?>> en = (Class<Enum<?>>) forName(fullName);
		return en.getEnumConstants();
	}

	public static Enum<?>[] getEnums(Class<Enum<?>> clazz) {
		return clazz.getEnumConstants();
	}

	@SuppressWarnings("cast")
	public static Class<?> forName(String fullName) {
		Class<?> en = null;
		try {
			en = (Class<?>) Class.forName(fullName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return en;
	}

	/** 인스턴스를 리턴한다. (예외 catch때문에 사용.) */
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 최초 1회만 실행. List에 최초의 requestParameters(size)만큼 자식 객체 생성 Dissolver에서 리플렉션시
	 * 사용된다.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List initIfCollectionIsNull(List subBeanlist, Class subBeanClass, int size) {
		if (size == 0 || subBeanlist != null) return subBeanlist;
		subBeanlist = new ArrayList();
		for (int x = 0; x < size; x++) {
			Object subBean;
			try {
				subBean = subBeanClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			subBeanlist.add(subBean);
		}
		return subBeanlist;
	}
	
	/** 모든 상속구조의 필드를 다 조사하며, static인것은 제외한다.
	 * 확실하지 않지만, 천만건 이상 돌리면, 매번 호출하는거보나 약간 빠르다. 천전 이하에서는 매번 호출이 더 빠름.  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Field> getAllDeclaredFields(Class clazz){
		List<Field> fields = new ArrayList<Field>();
		List<Class> classes = ClassUtils.getAllSuperclasses(clazz); 
		CollectionUtil.addToList(fields, clazz.getDeclaredFields());
		for(Class each : classes){
			if(each==Object.class) continue;
			CollectionUtil.addToList(fields, each.getDeclaredFields());
		}
		Iterator<Field> i = fields.iterator();
		while(i.hasNext()){
			if(Modifier.isStatic(i.next().getModifiers())) i.remove();
		}
		return fields;
	}
	
	/** access를 true로 설정 후 Map으로 리턴한다. */
	@SuppressWarnings("rawtypes")
	public static Map<String,Field> getAllDeclaredFieldMap(Class clazz){
		Map<String,Field> fieldMap = Maps.newHashMap();
		List<Field> fields = getAllDeclaredFields(clazz);
		for(Field each : fields){
			each.setAccessible(true);
			fieldMap.put(each.getName(), each);
		}
		return fieldMap;
	}
	
	/** access를 true로 설정 후 Map으로 리턴한다.
	 * 카멜케이스를 DB에서 사용하는 언더스코어로 변경해서 리턴한다. */
	@SuppressWarnings("rawtypes")
	public static Map<String,Field> getAllDeclaredFieldUnderscoreMap(Class clazz){
		Map<String,Field> fieldMap = Maps.newHashMap();
		List<Field> fields = getAllDeclaredFields(clazz);
		for(Field each : fields){
			each.setAccessible(true);
			fieldMap.put(StringUtil.getUnderscore(each.getName()), each);
		}
		return fieldMap;
	}


	
	@SuppressWarnings("rawtypes")
	public static Class<?> getGeneric(Field field){
		ParameterizedType pt = (ParameterizedType)field.getGenericType();
		return (Class)pt.getActualTypeArguments()[0];
	}

	/**
	 * Setter Method의 Collection제너릭 타입 이름을 추출한다. Class에서 제너릭이 붙은 method는 오직
	 * ex) public void setBooks(List<Book> books) {  => Book.class가 리턴
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?> getGeneric(Method method){
		ParameterizedType pt = (ParameterizedType)method.getGenericParameterTypes()[0];
		return (Class)pt.getActualTypeArguments()[0];
	}

	/**
	 * clazz의 fieldName에 적합하게 value를 캐스팅해서 리턴한다. 단순 String타입의 value를 적절해 캐스팅한
	 * ,Object로 보내줘야 하는 HibernateCriterion에서 사용한다. enum의 경우 특수하게 표현
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T getCastedValue(Class<?> clazz, String fieldName, String value) {
		Class<T> field;
		try {
			field = (Class<T>) clazz.getDeclaredField(fieldName).getType();
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		if (field.isEnum()) return (T) Enum.valueOf((Class) field, value);
		return field.cast(value);

	}

	public static String getPackageName(Class<?> clazz) {
		return ClassUtils.getPackageName(clazz);
	}

	/** Class의 파일을 가져온다. Groovy 디버깅 용이다. root는 jvm이 실행된 경로를 말한다. */
	public static File sourceFile(Class<?> clazz) {
		final String src = "src/";
		String name = clazz.getName().replaceAll("\\.", "/");
		File file = new File(src + name + ".java");
		if (file.exists()) return file;
		file = new File(src + name + ".groovy");
		if (file.exists()) return file;
		throw new IllegalArgumentException(clazz + " not fount");
	}

	/** ReflectionUtils.findField에 추가해서 값을 가져온다. 특이한 케이스에만 사용하자. */
	@SuppressWarnings("unchecked")
	public static <T> T findFieldValue(Object instance, String name) {
		Field f = ReflectionUtils.findField(instance.getClass(), name);
		f.setAccessible(true);
		try {
			return (T) f.get(instance);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/** getter의 name에 해당하는 리턴class를 리턴한다. */
	public static Class<?> getterReturnClass(Class<?> clazz, String name) {
		if (!name.startsWith("get")) name = "get" + WordUtils.capitalize(name);
		try {
			return clazz.getMethod(name).getReturnType();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/** 인스턴스의 필드중에서 해당하는 이름의 setter에 객체를 할당한다. */
	@Deprecated
	public static void setObject(Object instance, String fieldName, Object value) {
		Class<?> clazz = instance.getClass();
		Method setter = ReflectionUtil.toSetter(clazz, fieldName, value.getClass());
		try {
			setter.invoke(instance, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/** 위에꺼 대체? */
	public static void setField(Object instance, String fieldName, Object value) {
		setField(instance.getClass(),instance,fieldName,value);
	}
	@SuppressWarnings("rawtypes")
	public static void setField(Class clazz,Object instance, String fieldName, Object value) {
		try {
			ReflectionUtil.setField(clazz.getField(fieldName), instance, value);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * getter인가?
	 */
	public static boolean isGetter(Method method) {
		String name = method.getName();
		return StringUtil.getFieldName(name) == null ? false : true;
	}

	/** setter로 getter를 리턴한다. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Method toSetter(Class clazz, String fieldName, Class param) {
		String setterName = "set" + StringUtil.capitalize(fieldName);
		Method setter = null;
		try {
			setter = clazz.getMethod(setterName, param);
		} catch (NoSuchMethodException e) {

		}
		return setter;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Method toGetter(Class clazz, String fieldName) {
		String getterName = "get" + StringUtil.capitalize(fieldName);
		Method getter = null;
		try {
			getter = clazz.getMethod(getterName);
		} catch (NoSuchMethodException e) {
			String getterName2 = "is" + StringUtil.capitalize(fieldName);
			try {
				getter = clazz.getMethod(getterName2);
			} catch (NoSuchMethodException e1) {
				return null;
			}
		}
		return getter;
	}

	/** 메소드 이름과 args의 수 만으로 메소드를 찾아낸다. 특수목적용., 더 좋은 방법이 있을듯. */
	public static Method getMethodByName(Class<?> clazz, String name, int argsSize) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods)
			if (method.getName().equals(name) && method.getParameterTypes().length == argsSize) return method;
		return null;
	}
	
    /** 해당하는 이름의 필드명이 있다면 값을 입력한다. 아니면 무시한다. */
    public static void setValueIfAble(Object newObject,String fieldName,Object value){
        try {
            Field field =  newObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(newObject, value);
        } catch (NoSuchFieldException e) {
            return;  //무시한다.
        } catch (Exception e) {
            throw new RuntimeException(e);            
        }
    }
    
    /** null포함, 단순히 이름 매칭으로 데이터의 래퍼런스만을 모두  복사한다. 간단한 이력에만 사용하도록 하자.   */
    public static void shallowCopyAllByName(Object server,Object newObject){
        Class<?> clazz = (Class<?>) server.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(server);
            } catch (Exception e) {
                throw new RuntimeException(e);            
            }
            setValueIfAble(newObject, field.getName(), value);
        }
    }
    
    /** 간단버전 */
    public static <T> T shallowCopyAllByName(Object server,Class<T> newClass){
        T obj = newInstance(newClass);
        shallowCopyAllByName(server,obj);
        return obj;
    }
    

    /** 마이바티스 때문에 빡쳐서 만듬 */
    public static List<String> findNullObject(Object server){
        List<String> list = new ArrayList<String>();
        Class<?> clazz = (Class<?>) server.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(server);
            } catch (Exception e) {
                throw new RuntimeException(e);            
            }
            if(value==null) list.add(field.getName());
        }
        return list;
    }
    
    /** 해당 클래스의 모든 필드를 null로 초기화한다.
     * jsp 태그에 사용할목적으로 만듬.
     *  Primitive와 상속클래스는 제외된다. */
    public static void initObject(Object object){
        Field[] fs = object.getClass().getDeclaredFields();
        for(Field each : fs){
            each.setAccessible(true);
            if(!each.getType().isPrimitive()) setField(each, object, null);
        }
    }
    
    /** 간단 복사. A값이 null이고 B값이 null이 아니라면 B의값을 A로 복사한다. */
    public static <T> void  shallowCopyNull(T a,T b){
        @SuppressWarnings("unchecked")
        //Class<T> clazz =  (Class<T>) a.getClass();
        Class<T> clazz =  (Class<T>) b.getClass(); //A에는 있으나 B에는 없는 필드는 복사하지 않는다.  ex) A extends B
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            int mod = field.getModifiers();
            if(Modifier.isStatic(mod)) continue;
            if(Modifier.isFinal(mod)) continue;
            field.setAccessible(true);
            try {
                Object valueOfB = field.get(b);
                if(valueOfB==null) continue;
                Object valueOfA = field.get(a);
                if(valueOfA!=null) continue;
                field.set(a, valueOfB);
            } catch (Exception e) {
                throw new RuntimeException(e);            
            }
        }
    }
    
    /** 간단 복사. A값이 null이고 B값이 null이 아니라면 B의값을 A로 복사한다. */
    public static <T> void  shallowCopy(T a,T b){
        @SuppressWarnings("unchecked")
        Class<T> clazz =  (Class<T>) b.getClass(); //A에는 있으나 B에는 없는 필드는 복사하지 않는다.  ex) A extends B
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            int mod = field.getModifiers();
            if(Modifier.isStatic(mod)) continue;
            if(Modifier.isFinal(mod)) continue;
            field.setAccessible(true);
            try {
                Object valueOfB = field.get(b);
                field.set(a, valueOfB);
            } catch (Exception e) {
                throw new RuntimeException(e);            
            }
        }
    }
    
    //nested loop
    
    /** masters 기준으로 slaves 값을 masters로 복사한다. masters가 많고 slaves가 적어야 한다.
     * 주의!  slave에는 있지만 master에는 없는 값은 무시된다.
     * 두개의 관계형 DB에서 데이터를 각가 가져올때 사용한다.
     * 성능에 민감하다면 shallowCopyNull 대신 콜백을 사용할것! */
    public static <T extends EntityId<String>> void  nestedLoopJoin(List<T> masters,List<T> slaves){
        Map<String,T> slaveMap = new HashMap<String,T>();
        for(T eachSlave : slaves) slaveMap.put(eachSlave.getId(), eachSlave);
        for(T master : masters){
            T slave = slaveMap.get(master.getId());
            if(slave==null) continue;
            if(slave==master) continue;
            ReflectionUtil.shallowCopyNull(master, slave);
        }
    }
    
    /** 모든 객체를 key 중심으로 조인한다.  VO에는 두개의 테이블A,B의 컬럼 모두가 포함되어야 한다. 
     * 결과는 걍 첫번째 객체 기준으로  value들을 더해서 리턴한다.(입력인자중 어느게 될지 모름) */
    public static <T extends EntityId<String>> List<T>  hashJoin(List<T> ... listArray){
        MapForList<T> map = new MapForList<T>();
        for(List<T> list : listArray) for(T each : list) map.add(each.getId(), each);
        
        List<T> result = new ArrayList<T>();
        for (Entry<String, List<T>> entry : map.entrySet()) {
            List<T> value = entry.getValue();
            if(value.size()==1) result.add(value.get(0));
            else{
                T firstValue = value.get(0);
                for(int i=1;i<value.size();i++) ReflectionUtil.shallowCopyNull(firstValue, value.get(i));
                result.add(firstValue);
            }
        }
        return result;
        
    }
    
    /** null값에 0을 넣어준다. */
    public static <T> void  nullValueToZero(T a){
        @SuppressWarnings("unchecked")
        Class<T> clazz =  (Class<T>) a.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            Class<?> type = field.getType();
            boolean isNumber = Number.class.isAssignableFrom(type);
            if(!isNumber) continue;
            field.setAccessible(true);
            try {
                Object value = field.get(a);
                if(value!=null) continue;
                if(type.isAssignableFrom(Long.class)) field.set(a, 0L);
                else if(type.isAssignableFrom(Integer.class)) field.set(a, 0);
                else if(type.isAssignableFrom(BigDecimal.class)) field.set(a, BigDecimal.ZERO);
            } catch (Exception e) {
                throw new RuntimeException(e);            
            }
        }
    }
    
    /** 이름하고 매치되는 아무 메소드 1개를 리턴한다. */
    public static Method getMethodByName(Class<?> clazz, String name) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods)
            if (method.getName().equals(name) ) return method;
        return null;
    }
    
    /** 해당 어노테이션이 매핑된 모든 메소드를 가져온다 */
    public static List<Method> getMethodByAnnotation(Class<?> clazz, Class<? extends Annotation> anno) {
        List<Method> result = new ArrayList<Method>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) if(method.isAnnotationPresent(anno)) result.add(method);
        return result;
    }
    
    /** Method를 래피한다.
     * 숨겨진 메소드를 실행하는 용도로 사용 */
    public static class Methods{
    	public final Method method;
    	/** 정확한 대상 clazz를 명시해야 한다. */
    	public Methods(Class<?> clazz,String methdName,Class<?> ... parameterTypes){
    		try {
				method = clazz.getDeclaredMethod(methdName, parameterTypes);
				method.setAccessible(true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	}
		public Object invoke(Object arg0, Object... arg1){
			try {
				return method.invoke(arg0, arg1);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
    }
    
    /** private으로 접근 불가능한 필드를 사용하고 싶을때 쓰자 */
    public static class Fields{
    	public final Field field;
    	/** 정확한 대상 clazz를 명시해야 한다. */
    	public Fields(Class<?> clazz,String methdName){
    		try {
    			field = clazz.getDeclaredField(methdName);
    			field.setAccessible(true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	}
		public Object get(Object arg0){
			try {
				return field.get(arg0);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
    }
    

}