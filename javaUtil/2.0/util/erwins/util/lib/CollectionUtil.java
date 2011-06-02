package erwins.util.lib;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Collection과 Array에 관한 Util이다. java.util.EnumSet을 잘 활용할것! 경고!!! T ... items 에
 * 배열이 오면 배열로 인식하지만 collection이 올 경우 하나의 T로 인식해 버린다. 주의
 * 
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class CollectionUtil extends CollectionUtils {
	
	/** 키값 문자열사이즈의 역순정렬 */
	@SuppressWarnings("rawtypes")
	public static Comparator<Entry> ENTRY_KEY_LENGTH_RE = new Comparator<Entry>() {
		@Override
		public int compare(Entry o1, Entry o2) {
			int a = o1.getKey().toString().length(); 
			int b = o2.getKey().toString().length();
			if(a==b) return 0;
			else return a < b ? 1 : -1;
		}
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Entry> sortMap(final Map map,Comparator<Entry> comparator) {
		List<Entry> list = new ArrayList<Entry>();
		list.addAll(map.entrySet());
		Collections.sort(list, comparator);
		return list;
	}

	/** Groovy등에서 사용 */
	@SuppressWarnings("rawtypes")
	public static Comparator<Map> mapComparator(final String key) {
		Comparator<Map> c = new Comparator<Map>() {
			@Override
			public int compare(Map o1, Map o2) {
				return o1.get(key).toString().compareTo(o2.get(key).toString());
			}
		};
		return c;
	}
	
	/** Groovy등에서 사용 */
	@SuppressWarnings({ "rawtypes" })
	public static void sortByKey(List<Map> list, final String key) {
		Collections.sort(list, mapComparator(key));
	}

	/**
	 * 얕은 복사를 수행한다.
	 */
	public static <T> List<T> copy(List<T> c) {
		List<T> result = new ArrayList<T>(c.size());
		result.addAll(c);
		return result;
	}

	/** 빈 배열인지. */
	public static <T> boolean isEmpty(T[] t) {
		if (t == null || t.length == 0) return true;
		return false;
	}

	/**
	 * 소팅을 해 보아요~ 조건이 간단하고 순차 졍렬만 지원
	 */
	public static <T extends Comparable<T>> List<T> sort(List<T> list) {
		Collections.sort(list, new Comparator<T>() {
			public int compare(T dom1, T dom2) {
				return dom1.compareTo(dom2);
			}
		});
		return list;
	}

	/**
	 * DataAccessUtils과 유사함. Collection에서 Unique값을 추출해 낸다. 수정 핋요할듯..
	 */
	public static Integer getResultInt(List<Object> list) {
		if (list == null) throw new IllegalArgumentException("list is null . collection must be not null");
		if (list.size() != 1) throw new IllegalArgumentException(list.size() + " collection must be unique");
		Object obj = list.get(0);
		if (obj instanceof BigDecimal) return ((BigDecimal) obj).intValue();
		return (Integer) list.get(0);
	}

	/**
	 * DataAccessUtils과 유사함. Collection에서 Unique값을 추출해 낸다.
	 */
	public static Number getResultCount(List<Object> list) {
		if (list == null) throw new IllegalArgumentException("list is null . collection must be not null");
		if (list.size() != 1) throw new IllegalArgumentException(list.size() + " collection must be unique");
		Object obj = list.get(0);
		if (obj instanceof BigDecimal) return ((BigDecimal) obj).longValue();
		return (Number) obj;
	}

	/**
	 * DataAccessUtils과 유사함. Collection에서 Unique값을 추출해 낸다.
	 */
	public static <T> T getResultUnique(Collection<T> list) {
		if (list == null) throw new IllegalArgumentException(" collection is null! ");
		else if (list.size() != 1) throw new IllegalArgumentException(list.size() + " collection must be unique : "
				+ list.iterator().next().toString());
		else return list.iterator().next();
	}

	/** 1개만 추출해 낸다. 없다면 null을 리턴한다. */
	public static <T> T getUniqNullable(Collection<T> sets) {
		if (sets.size() == 0) return null;
		else if (sets.size() == 1) return sets.iterator().next();
		else throw new IllegalArgumentException(sets.size() + " collection must be unique or zero size : "
				+ sets.iterator().next().toString());
	}

	/**
	 * 마지막 객체를 반환한다.
	 */
	public static <T> T getLast(List<T> list) {
		if (list == null || list.size() == 0) return null;
		return list.get(list.size() - 1);
	}

	public static <T> T getLast(T[] list) {
		return list[list.length - 1];
	}

	// ===========================================================================================
	// 비교하기 각기 3종류를 가진다.
	// ===========================================================================================

	/**
	 * ==으로 비교한다.
	 */
	public static <T> boolean isSameAny(T body, T... items) {
		if (body == null || items.length == 0) return false;
		for (T item : items)
			if (body == item) return true;
		return false;
	}

	/**
	 * 배열에 해당 물품을 가지고 있는지 검사한다. 하나라도 있으면 true를 리턴한다. T에 collection이 오면 안된다.
	 */
	public static <T> boolean isEqualsAny(T[] bodys, T... items) {
		if (bodys == null || items.length == 0) return false;
		for (T body : bodys)
			for (T item : items)
				if (item.equals(body)) return true;
		return false;
	}

	public static <T> boolean isEqualsAny(T[] bodys, Collection<T> items) {
		if (bodys == null || items.size() == 0) return false;
		for (T body : bodys)
			for (T item : items)
				if (item.equals(body)) return true;
		return false;
	}

	/**
	 * 단일 물품의 값과 배열내의 값을. 비교한다. 하나라도 있으면 true를 리턴한다.
	 */
	public static <T> boolean isEqualsAny(Collection<T> bodys, T... items) {
		if (bodys == null || items.length == 0) return false;
		for (T body : bodys)
			for (T item : items)
				if (item.equals(body)) return true;
		return false;
	}

	/**
	 * 단일 물품의 값과 배열내의 값을. 비교한다. 하나라도 있으면 true를 리턴한다.
	 */
	public static <T> boolean isEqualsAny(T body, T... items) {
		if (body == null || items.length == 0) return false;
		for (T item : items)
			if (item.equals(body)) return true;
		return false;
	}

	/**
	 * 제우스 긍 ~~~
	 */
	public static boolean isAssignableFrom(Class<?> body, Class<?>... items) {
		if (body == null || items.length == 0) return false;
		for (Class<?> item : items)
			if (item.isAssignableFrom(body)) return true;
		return false;
	}

	/**
	 * 배열에 null이 있는지 확인한다. 하나라도 있으면 true를 리턴한다. 배열의 size가 0이면 false이다.
	 */
	public static <T> boolean isNullAny(T... items) {
		for (T item : items)
			if (item == null) return true;
		return false;
	}

	/**
	 * 배열에 해당 물품의 클래스를 가지고 있는지 검사한다. 하나라도 있으면 true를 리턴한다. 다중인자에서 T사용은 안되는구나. ㅠㅠ
	 * ex) if(Sets.isInstance(annos,Hidden.class)) continue; 티백스의 제우스에서는 오류난다~
	 * ㅋㅋ Method클래스의 isAnnotationPresent(Hidden.class)를 사용할것!
	 */
	/*
	 * @Deprecated public static <T> boolean isInstanceAny(T[] bodys , Class<?
	 * extends T> ... clazzs) { if(bodys==null || clazzs.length==0) return
	 * false; for(T each : bodys) for(Class<? extends T> clazz : clazzs)
	 * if(clazz.isInstance(each)) return true; return false; }
	 */

	public static boolean isAnnotationPresent(Method bodys, Class<? extends Annotation>... clazzs) {
		for (Class<? extends Annotation> each : clazzs)
			if (bodys.isAnnotationPresent(each)) return true;
		return false;
	}

	/**
	 * 배열에 해당 물품의 클래스를 가지고 있으면 리턴한다. ex) Enumerated enumerated =
	 * CollectionUtil.getIsInstance(annos, Enumerated.class);
	 */
	@SuppressWarnings("unchecked")
	public static <Super, T extends Super> T getIsInstance(Super[] bodys, Class<T> clazz) {
		if (bodys == null || clazz == null) return null;
		for (Super each : bodys)
			if (clazz.isInstance(each)) return (T) each;
		return null;
	}

	// ===========================================================================================
	// null safe
	// ===========================================================================================

	/**
	 * null sasfe하게 list의 사이즈를 구한다.
	 */
	public static Integer getSize(List<?> list) {
		if (list == null) return 0;
		return list.size();
	}

	/**
	 * 배열을 List로 반환한다.
	 */
	public static <T> List<T> toList(T... a) {
		if (a == null) return Collections.emptyList();
		// list = Arrays.asList(a);
		List<T> list = new ArrayList<T>();
		for (T each : a)
			list.add(each);
		return list;
	}

	// ===========================================================================================
	// etc
	// ===========================================================================================

	/**
	 * 형을 알 수 없는 obj를 List로 바꾼다. toList를 형을 알 수 없은 상태로 사용할 경우 배열채로 List에 들어가
	 * 버린다. 따라서 이 메소드를 사용하자.
	 */
	public static List<String> toStringList(Object obj) {
		if (obj instanceof String) return toList((String) obj);
		else if (obj instanceof String[]) return toList((String[]) obj);
		else return Collections.emptyList();
	}

	/**
	 * null safe하게 list의 개체를 반환한다. i가 최대값보다 크거나 음수일 경우 null을 리턴한다.
	 */
	public static <T> T getObject(List<T> a, int i) {
		int size = a.size();
		if (i >= size || i < 0) return null;
		return a.get(i);
	}

	/**
	 * Map에 담긴 List(문자배열형식)를 => List에 담긴 Map으로 변환한다. getElementsByTagName등으로 개별
	 * 속성을 배열로 가져올 경우 이것을 객체별로 분류할때 사용된다. Map에 담긴 List의 사이즈가 모두 동일해야 한다.
	 */
	public static List<HashMap<String, String>> swap(HashMap<String, List<String>> map) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		// 초기화
		int maxSize = map.values().iterator().next().size();
		for (int i = 0; i < maxSize; i++)
			list.add(new HashMap<String, String>());

		// 변환작업
		for (Entry<String, List<String>> entry : map.entrySet()) {
			List<String> thisList = entry.getValue();
			int thisSize = thisList.size();
			if (thisSize != maxSize) throw new IllegalArgumentException(maxSize + " : " + thisSize + "사이즈가 균일하지 않음");
			for (int i = 0; i < thisSize; i++) {
				list.get(i).put(entry.getKey(), thisList.get(i));
			}
		}

		return list;
	}

	/**
	 * 오라클 CLOB대신 VC를 사용함 getByte()는 너무 많은 리소스 사용하기때문에 한글 800자 기준으로 문자열을 쪼개줌
	 * vc(4000) 800은 경험치임.. UTF-8은 한글이 3바이트 이게 쓰이면 잘못된거임
	 */
	@Deprecated
	public static List<String> getOracleStr(String str) {
		if (str.length() == 0) return Collections.emptyList();
		List<String> list = new ArrayList<String>();
		int oracleMaxCaracter = 800;
		int totalCount = str.length() / oracleMaxCaracter + 1;
		for (int i = 0; i < totalCount; i++) {
			if (i == totalCount - 1) list.add(str.substring(i * oracleMaxCaracter, str.length()));
			else list.add(str.substring(i * oracleMaxCaracter, (i + 1) * oracleMaxCaracter));
		}
		return list;
	}

	/**
	 * 분산된 컬럼을 List에 담아 하나의 String으로 반환함
	 */
	@Deprecated
	public static String getOracleStr(List<String> list) {
		if (list == null) return StringUtils.EMPTY;
		StringBuffer stringBuffer = new StringBuffer();
		for (String string : list)
			stringBuffer.append(string);
		return stringBuffer.toString();
	}

	/**
	 * 배열을 역산해서 리턴한다.
	 */
	public static <T> List<T> inverse(List<T> list) {
		List<T> inversed = new ArrayList<T>();
		for (int i = list.size(); i > 0; i--) {
			inversed.add(list.get(i - 1));
		}
		return inversed;
	}

	/**
	 * 일치하는 값이 없을때만 obj를 추가한다. 자료에 순사가 있어 Set을 사용할 수 없을때 사용한다. 자료가 작을때만 사용 가능하다.
	 */
	public static <T> void addIfNotFound(List<T> list, T obj) {
		for (T value : list)
			if (value.equals(obj)) return;
		list.add(obj);
	}

	/**
	 * List에 Array를 추가한다. ex) addToList(fields, clazz.getDeclaredFields());
	 */
	public static <T> void addToList(List<T> fields, T[] array) {
		for (T e : array)
			fields.add(e);
	}

	/**
	 * Line이 잘린 컬럼을 머지한다. ex) A,B,C,D 와 1,2,3,4를 머지하면 A,B,C,D1,2,3,4 이렇게 합쳐진다.
	 */
	public static String[] mergeForLineSeperated(String[] A, String[] B) {
		String[] sum = new String[A.length + B.length - 1];
		for (int i = 0; i < A.length; i++) {
			sum[i] = A[i];
		}
		sum[A.length - 1] += B[0];
		for (int i = 1; i < B.length; i++) {
			sum[A.length - 1 + i] = B[i];
		}
		return sum;
	}

}