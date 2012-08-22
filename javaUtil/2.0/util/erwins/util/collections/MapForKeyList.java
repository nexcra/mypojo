package erwins.util.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.map.ListOrderedMap;


/**
 * 이하의 간단 버전이다.
 * private Map<T,List<File>> map = new HashMap<T,List<File>>();
 * Map을 구현하도록 바꿨다. (JSTL때문에)
 */
@SuppressWarnings("serial")
public class MapForKeyList<K,T> implements Map<K,List<T>> ,Serializable{
	
	private Map<K,List<T>> map;
	private ListInstanceCallback<T> callback;
	
	public void setCallback(ListInstanceCallback<T> callback) {
		this.callback = callback;
	}
	
	public static interface ListInstanceCallback<T>{
		public List<T> getListInstance();
	}

	@SuppressWarnings("unchecked")
	public MapForKeyList(MapType type){
		switch(type){
		case Hash :  map = new HashMap<K,List<T>>(); break;
		case Tree :  map = new TreeMap<K,List<T>>(); break;
		case ListOrderd :  map = new ListOrderedMap(); break;
		}
	}
	
	public MapForKeyList<K,T> add(K key,T item){
		List<T> list = map.get(key);
		if(list==null){
			if(callback==null) list = new ArrayList<T>();
			else list = callback.getListInstance();
			map.put(key, list);
		}
		list.add(item);
		return this;
	}
	
	public List<T> get(Object key){
		return map.get(key);
	}
	/** Groovy용. 이놈은 타입이 딱맞아야 한다(Object로 안됨) ㅅㅂ.  */
	public List<T> getAt(String key){
		return map.get(key);
	}
	
	/** 각 key당 중복된 item은 들어갈 수 없다. */
	public MapForKeyList<K,T> addUnique(K key,T item){
		List<T> list = map.get(key);
		if(list==null){
			list = new ArrayList<T>();
			map.put(key, list);
		}
		for(T each : list) if(each.equals(item)) return this;
		list.add(item);
		return this;
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

	public Set<Entry<K, List<T>>> entrySet() {
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

	public List<T> put(K key, List<T> value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends List<T>> m) {
		map.putAll(m);
	}

	public List<T> remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<List<T>> values() {
		return map.values();
	}
	
    
}