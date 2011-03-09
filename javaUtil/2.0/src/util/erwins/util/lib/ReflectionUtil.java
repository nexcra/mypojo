package erwins.util.lib;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import erwins.util.exception.BusinessException;
import erwins.util.root.Pair;

/**
 * 이놈은 예외로 커먼스를 사용하지 않는다. (없다) Generic정보는 객체에서는 사라지지만 Class에는 남아있어 런타임에 사용 가능하다.
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
	public static Class<?> genericClass(Class<?> clazz, int index) {
		ParameterizedType genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
		return (Class<?>) genericSuperclass.getActualTypeArguments()[index];
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
	private static Class<?> forName(String fullName) {
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Field> getAllDeclaredFields(Class clazz){
		List<Field> fields = new ArrayList<Field>();
		List<Class> classes = ClassUtils.getAllSuperclasses(clazz); 
		CollectionUtil.addToList(fields, clazz.getDeclaredFields());
		for(Class each : classes){
			if(each==Object.class) continue;
			CollectionUtil.addToList(fields, each.getDeclaredFields());
		}
		return fields;
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
	public static <T> T findFieldValue(T instance, String name) {
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
	public static void setObject(Object instance, String fieldName, Object value) {
		Class<?> clazz = instance.getClass();
		Method setter = ReflectionUtil.toSetter(clazz, fieldName, value.getClass());
		try {
			setter.invoke(instance, value);
		} catch (Exception e) {
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

}