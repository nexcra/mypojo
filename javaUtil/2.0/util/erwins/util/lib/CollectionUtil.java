package erwins.util.lib;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * 주의. Arrays,Collections,CollectioUtils,ArrayUtils 먼저 보고 없으면 추가.
 * 
 * Collection과 Array에 관한 Util이다. java.util.EnumSet을 잘 활용할것! 경고!!! T ... items 에
 * 배열이 오면 배열로 인식하지만 collection이 올 경우 하나의 T로 인식해 버린다. 주의
 */
public abstract class CollectionUtil extends CollectionUtils {
	
	/** 제너릭 충돌 방지를 위해 따로 설정한다. */
	public static final Set<String> EMPTY_STRING_SET = ImmutableSet.of();

	/**
	 * DataAccessUtils과 유사함. Collection에서 Unique값을 추출해 낸다.
	 * SQL 전체카운트 등을 실행했을때 
	 */
	public static Number getNumber(List<?> list) {
		if (list == null) throw new IllegalArgumentException("list is null . collection must be not null");
		if (list.size() != 1) throw new IllegalArgumentException(list.size() + " collection must be unique");
		Object obj = list.get(0);
		if (obj instanceof Number) return ((Number) obj);
		throw new IllegalArgumentException("list result is not number");
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
	 * ==으로 비교한다. ????
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
	@Deprecated
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
	@Deprecated
	public static <T> boolean isEqualsAny(T body, T... items) {
		if (body == null || items.length == 0) return false;
		for (T item : items)
			if (item.equals(body)) return true;
		return false;
	}

	/**
	 * 배열에 null이 있는지 확인한다. 하나라도 있으면 true를 리턴한다. 배열의 size가 0이면 false이다.
	 */
	@Deprecated
	public static <T> boolean isNullAny(T... items) {
		for (T item : items)
			if (item == null) return true;
		return false;
	}


	// ===========================================================================================
	// etc
	// ===========================================================================================

	/**
	 * null safe하게 list의 개체를 반환한다. i가 최대값보다 크거나 음수일 경우 null을 리턴한다.
	 */
	public static <T> T nullSafeGet(List<T> list, int i,T nullObject) {
		int size = list.size();
		if (i >= size || i < 0) return nullObject;
		return list.get(i);
	}
	
    /** index가 짧거나, null이거나 String empty이면 nullObject를 반환한다.
     * Poi를 읽을때 사용한다. */
    public static <T> T nullSafeGet(T[] array,int index,T nullObject){
    	if(array.length <= index) return nullObject;
    	T result = array[index];
    	if(result==null) return nullObject;
    	return result;
    }
	
    
	

}