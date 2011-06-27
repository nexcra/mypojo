package erwins.util.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections.map.ListOrderedMap;


/**
 * 이하의 간단 버전이다.
 * private Map<T,List<File>> map = new HashMap<T,List<File>>();
 */
@SuppressWarnings("serial")
public class MapForKeyList<K,T> implements Iterable<Entry<K, List<T>>>,Serializable{
	
	private Map<K,List<T>> map;
	
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
			list = new ArrayList<T>();
			map.put(key, list);
		}
		list.add(item);
		return this;
	}
	
	public List<T> get(K key){
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

	@Override
	public Iterator<Entry<K, List<T>>> iterator() {
		return map.entrySet().iterator();
	}
	
    
}