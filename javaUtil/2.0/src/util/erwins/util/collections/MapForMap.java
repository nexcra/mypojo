package erwins.util.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;



/**
 * 이하의 간단 버전이다. Map<String,Map<String,T>>
 */

public class MapForMap<T> implements Iterable<Entry<String, Map<String,T>>>{
	
	private Map<String,Map<String,T>> body;

	public MapForMap(MapType type){
		switch(type){
		case Hash :  body = new HashMap<String,Map<String,T>>(); break;
		case Tree :  body = new TreeMap<String,Map<String,T>>(); break;
		}
	}
	
	public void add(String parentKey,String key,T value){
		Map<String,T> map = body.get(parentKey); 
		if(map==null){
			map = new HashMap<String,T>();
			body.put(parentKey, map);
		}
		map.put(key,value);
	}
	
	/** 보통 TreeMap으로 인스턴스를 만들어야 한다.
	 *  List를 메인 키를 가지는 Map으로 바꿔 준다. List의 key가 동일하나 내용이 다른 중복데이터는 Map으로 합쳐진다. 
	 * 파라메터는 반드시 3가지여야 한다. parentKey,header(내부Map의 key),value
     * value를 제외하면 모두 null이 될 수 없다.*/
	@SuppressWarnings("unchecked")
	public void merge(List<Object[]> args) {
		for(Object[] each :args){
			if(each.length!=3) throw new IllegalArgumentException(each.length + "args length must be 3!");
			String parentKey = each[0].toString();
			add(parentKey,each[1].toString(),(T)each[2]);
		}
	}

	@Override
	public Iterator<Entry<String, Map<String, T>>> iterator() {
		return body.entrySet().iterator();
	}
	
    
}