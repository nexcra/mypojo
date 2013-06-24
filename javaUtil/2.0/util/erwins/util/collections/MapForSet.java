package erwins.util.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;


/**
 * 이하의 간단 버전이다.
 * private Map<K,Set<T>> map = Maps.newHashMap();
 */
@SuppressWarnings("serial")
public class MapForSet<K,T> implements Map<K,Set<T>> ,Serializable{
	
	private Map<K,Set<T>> map = Maps.newHashMap();
	
	public MapForSet<K,T> add(K key,T item){
		Set<T> set = map.get(key);
		if(set==null){
			set = new HashSet<T>();
			map.put(key, set);
		}
		set.add(item);
		return this;
	}
	
	/** null을 리턴하지 않는다. */
	public Set<T> get(Object key){
		Set<T> v = map.get(key);
		if(v==null) v = Collections.emptySet();
		return v;
	}
	
	/** Groovy용. 이놈은 타입이 딱맞아야 한다(Object로 안됨) ㅅㅂ.  */
	public Set<T> getAt(String key){
		return map.get(key);
	}
	
	/* ================================================================================== */
	/*                          이하 위임                                                          */
	/* ================================================================================== */
	
	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<Entry<K, Set<T>>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public int hashCode() {
		return map.hashCode();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Set<T> put(K key, Set<T> value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends Set<T>> m) {
		map.putAll(m);
	}

	public Set<T> remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<Set<T>> values() {
		return map.values();
	}
	
    
}