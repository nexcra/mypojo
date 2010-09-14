package erwins.util.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;


/**
 * 이하의 간단 버전이다.
 * private Map<T,List<File>> map = new HashMap<T,List<File>>();
 */
public class ListedMap<K,T> implements Iterable<Entry<K, List<T>>>{
	
	private Map<K,List<T>> map;
	
	private ListedMap(){}
	
	public static <K,T> ListedMap<K,T> hashInstance(){
		ListedMap<K,T> instance = new ListedMap<K,T>();
		instance.map = new HashMap<K,List<T>>();
		return instance;
	}
	public static <K,T> ListedMap<K,T> treeInstance(){
		ListedMap<K,T> instance = new ListedMap<K,T>();
		instance.map = new TreeMap<K,List<T>>();
		return instance;
	}
	
	public ListedMap<K,T> add(K key,T item){
		List<T> list = map.get(key);
		if(list==null){
			list = new ArrayList<T>();
			map.put(key, list);
		}
		list.add(item);
		return this;
	}
	
	/** 각 key당 중복된 item은 들어갈 수 없다. */
	public ListedMap<K,T> addUnique(K key,T item){
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