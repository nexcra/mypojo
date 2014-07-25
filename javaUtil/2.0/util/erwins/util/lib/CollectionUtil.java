package erwins.util.lib;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.ListOrderedMap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	public static Number getNumber(Collection<?> list) {
		Object obj = getUnique(list);
		if (obj instanceof Number) return ((Number) obj);
		throw new IllegalArgumentException("list result is not instanceof Number");
	}

	/**
	 * DataAccessUtils과 유사함. Collection에서 Unique값을 추출해 낸다.
	 */
	public static <T> T getUnique(Collection<T> list) {
		if (list == null) throw new IllegalArgumentException(" collection is null! collection must be not null");
		else if (list.size() != 1) throw new IllegalArgumentException(list.size() + " collection must be unique : "
				+ list.iterator().next().toString());
		else return list.iterator().next();
	}

	/** getUnique()와 동일하지만  없다면 null을 리턴한다. */
	public static <T> T getUniqueNullable(Collection<T> list) {
		if (list == null) return null;
		else if (list.size() == 0) return null;
		else if (list.size() == 1) return list.iterator().next();
		else throw new IllegalArgumentException(list.size() + " collection must be unique or zero size : "
				+ list.iterator().next().toString());
	}
	
	public static <T> T getFirst(Collection<T> list) {
		if (list == null) return null;
		else if (list.size() == 0) return null;
		return list.iterator().next();
	}
	

	/**
	 * 마지막 객체를 반환한다.
	 */
	public static <T> T getLast(Collection<T> collection) {
		List<T> list = null;
		if(collection instanceof List){
			list = (List<T>) collection;		
		}else{
			list = Lists.newArrayList(collection);
		}
		if (list == null || list.size() == 0) return null;
		return list.get(list.size() - 1);
	}

	public static <T> T getLast(T[] list) {
		return list[list.length - 1];
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
    
    /** csvItemMapWriter때문에 만들었는데.. 쓸모가 없어짐. */
    public static <T> List<List<T>> splitBySize(Collection<T> input,int size) {
    	List<List<T>> result = Lists.newArrayList();
    	if(input.size()==0) return result;
    	if(!(input instanceof List)) input = Lists.newArrayList(input);
    	List<T>  list = (List<T>) input;
    	int maxIndex = input.size() / size;
    	int mode = input.size() % size;
    	for(int i=0;i<maxIndex;i++){
    		int start = i * size;
    		int end = (i+1) * size;
    		List<T> subList = list.subList(start, end);
    		result.add(subList);
    	}
    	if(mode > 0){
    		List<T> subList = list.subList(maxIndex * size, input.size());
    		result.add(subList);
    	}
    	return result;
    }
    
	/** 10개 max3 이면 처음 3개만 잘라서 리턴한다. */
	public static <T> List<T> subList(List<T> adEntry,int max){
		if(adEntry.size() > max) return adEntry.subList(0, max);
		return adEntry;
	}
	
	/** subList와 비슷하지만, 복사본을 리턴하지 않고, 해당 객체를 직접 수정한다. */
	public static <T> void removeAsOrder(List<T> adEntry,int max){
		if(adEntry.size() <= max) return;
		Iterator<T> it = adEntry.listIterator(max);
		while(it.hasNext()){
			it.next();
			it.remove();
		}
	}
	
    /** before 자료에 있던 자료와 동일한 순번으로 after를 리오더링 시켜준다.
     * 엑셀에 데이터를 이쁘게 넣어줄때 사용했다.  */
	public static <T> List<T> reOrderLikeBefore(List<T> before, List<T> after,T empty) {
 		List<T> cc = Lists.newArrayList();
 		Map<Integer,T> matched = Maps.newTreeMap();
 		for(T data : after){
 			int i = before.indexOf(data);
 			if(i < 0) cc.add(data); 
 			else matched.put(i, data);
 		}
 		
 		for(Entry<Integer, T> entry  : matched.entrySet()){
 			int index = entry.getKey();
 			while(cc.size() < index) cc.add(empty);
 			cc.add(index,entry. getValue());
 		}
 		return cc;
 	}
	
	/** 각각의 맵을 하나로 합친다.
	 * Map<String,Map<K,V>>  ==> Map<K,Map<String,V>>  
	 * 리턴되는 맵의 구현체는  newTreeMap / ListOrderedMap 이다 */
	@SuppressWarnings("unchecked")
	public static <K extends Comparable<K>,V> Map<K,Map<String,V>> mergeMap(Map<String,Map<K,V>> mergeSet) {
		
		Map<K,Map<String,V>> result = Maps.newTreeMap();
		for(Entry<String, Map<K, V>>  sourceEntry : mergeSet.entrySet()){
			Map<K, V> target = sourceEntry.getValue();
			for(Entry<K, V> e : target.entrySet()){
				Map<String,V> value = result.get(e.getKey());
				if(value==null){
					value = new ListOrderedMap();
					result.put(e.getKey(), value);
				}
				value.put(sourceEntry.getKey(), e.getValue());
			}
		}
		
		return result;
	}
	
	/**  Guava의 ArrayListMultiMap을 캐스팅할때 주로 사용된다.  */
	public static <T> List<T> toList(Collection<T> collection){
		if(collection instanceof List){
		}
		return Lists.newArrayList(collection);
	}

}