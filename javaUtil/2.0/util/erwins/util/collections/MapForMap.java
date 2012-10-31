package erwins.util.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.map.ListOrderedMap;



/**
 * 이하의 간단 버전이다. Map<String,Map<String,T>>
 * 그냥 써도 되고, 복잡한게 필요하면 body를 꺼내서 써도 된다.
 */
@SuppressWarnings("serial")
public class MapForMap<T> implements Map<String, Map<String,T>>,Serializable{
	
	private Map<String,Map<String,T>> body;
	/** 기본설정 해시 */
	private MapInstanceCallback<T> callback;
	
	public static interface MapInstanceCallback<T>{
		public Map<String,T> getMapInstance();
	}

	@SuppressWarnings("unchecked")
	public MapForMap(MapType type){
		switch(type){
		case Hash :  body = new HashMap<String,Map<String,T>>(); break;
		case Tree :  body = new TreeMap<String,Map<String,T>>(); break;
		case ListOrderd :  body = new ListOrderedMap(); break;
		}
	}
	
	public MapForMap(Map<String,Map<String,T>> map){
		this.body = map;
	}
	
	@SuppressWarnings("unchecked")
	public void add(String parentKey,String key,T value){
		Map<String,T> map = body.get(parentKey); 
		if(map==null){
			if(callback==null) map = new HashMap<String,T>();
			else map = callback.getMapInstance();
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

	public Iterator<Entry<String, Map<String, T>>> iterator() {
		return body.entrySet().iterator();
	}

	public int size() {
		return body.size();
	}

	public boolean isEmpty() {
		return body.isEmpty();
	}

	public boolean containsKey(Object key) {
		return body.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return body.containsValue(value);
	}

	public Map<String, T> get(Object key) {
		return body.get(key);
	}

	public Map<String, T> put(String key, Map<String, T> value) {
		return body.put(key, value);
	}

	public Map<String, T> remove(Object key) {
		return body.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Map<String, T>> m) {
		body.putAll(m);
	}

	public void clear() {
		body.clear();
	}

	public Set<String> keySet() {
		return body.keySet();
	}

	public Collection<Map<String, T>> values() {
		return body.values();
	}

	public Set<java.util.Map.Entry<String, Map<String, T>>> entrySet() {
		return body.entrySet();
	}

	public boolean equals(Object o) {
		return body.equals(o);
	}

	public int hashCode() {
		return body.hashCode();
	}

	public void setCallback(MapInstanceCallback<T> callback) {
		this.callback = callback;
	}

	public Map<String, Map<String, T>> getBody() {
		return body;
	}
	
	
    
}