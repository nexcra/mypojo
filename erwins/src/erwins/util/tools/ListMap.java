package erwins.util.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 이하의 간단 버전이다.
 * private Map<String,List<File>> map = new HashMap<String,List<File>>();
 */

public class ListMap<T> implements Iterable<Entry<String, List<T>>>{
	
	private Map<String,List<T>> map = new HashMap<String,List<T>>();
	
	public ListMap<T> add(String key,T item){
		List<T> list = map.get(key);
		if(list==null){
			list = new ArrayList<T>();
			map.put(key, list);
		}
		list.add(item);
		return this;
	}

	@Override
	public Iterator<Entry<String, List<T>>> iterator() {
		return map.entrySet().iterator();
	}
	
    
}