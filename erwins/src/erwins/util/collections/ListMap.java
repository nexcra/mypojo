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
 * private Map<String,List<File>> map = new HashMap<String,List<File>>();
 */

public class ListMap<T> implements Iterable<Entry<String, List<T>>>{
	
	private Map<String,List<T>> map;
	
	private ListMap(){}
	
	public static <T> ListMap<T> hashInstance(){
		ListMap<T> instance = new ListMap<T>();
		instance.map = new HashMap<String,List<T>>();
		return instance;
	}
	public static <T> ListMap<T> treeInstance(){
		ListMap<T> instance = new ListMap<T>();
		instance.map = new TreeMap<String,List<T>>();
		return instance;
	}
	
	public ListMap<T> add(String key,T item){
		List<T> list = map.get(key);
		if(list==null){
			list = new ArrayList<T>();
			map.put(key, list);
		}
		list.add(item);
		return this;
	}
	
	/** 각 key당 중복된 item은 들어갈 수 없다. */
	public ListMap<T> addUnique(String key,T item){
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
	public Iterator<Entry<String, List<T>>> iterator() {
		return map.entrySet().iterator();
	}
	
    
}