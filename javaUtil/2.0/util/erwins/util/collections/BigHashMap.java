package erwins.util.collections;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import erwins.util.root.NotThreadSafe;

/** 1000만건 이상의 데이터를 해시하기위한 꼼수. 정상작동하는지는 의문이다. */
@NotThreadSafe
public class BigHashMap<K,V> implements Map<K,V>{
	
	/** 문자열 8자리 기준이다. 최대치의 절반 정도가 효율이 좋은거 같다.  키가 더 크다면 이 숫자를 줄여야 한다. */
	private int limit = 10000000 / 2;
	private Map<K,V> current = Maps.newHashMap();
	
	@SuppressWarnings("unchecked")
	private final List<Map<K,V>> list = Lists.newArrayList(current);
	
	public BigHashMap(){}
	public BigHashMap(int limit ){ this.limit = limit ; }
	
	public void info() {
		System.out.println(list.size());
		for(Map<K,V> each : list){
			System.out.println(each.size());
		}
	}
	
	@Override
	public void clear() {
		for(Map<K,V> each : list) each.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V get(Object key) {
		for(Map<K,V> each : list) {
			V v = each.get(key);
			if(v!=null) return v;
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V put(K key, V value) {
		if(current.size() >= limit){
			current = Maps.newHashMap();
			list.add(current);
		}
		for(Map<K,V> each : list){
			if(each.containsKey(key)) return each.put(key, value); 
		}
		return current.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}
    

}
