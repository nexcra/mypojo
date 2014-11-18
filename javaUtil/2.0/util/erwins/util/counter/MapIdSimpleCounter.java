package erwins.util.counter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableLong;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import erwins.util.collections.AbstractMapSupport;

/**
 * 동기화가 없는 맵 카운터
 * @author sin
 */
@SuppressWarnings("serial")
public class MapIdSimpleCounter<ID> extends AbstractMapSupport<ID,Long> implements Iterable<Entry<ID,Long>>,Serializable {
    
	private Map<ID,MutableLong> map = Maps.newHashMap();

    public long incrementAndGet(ID key){
        return addAndGet(key,1L);
    }
    
    public long addAndGet(ID key,long delta){
    	MutableLong exist = map.get(key);
    	if(exist==null){
    		exist = new MutableLong(delta);
    		map.put( key, exist);
    	}else{
    		exist.add(delta);
    	}
    	return exist.longValue();
    }
    
    public MutableLong addAndGet(ID key) {
    	MutableLong exist = map.get(key);
    	if(exist==null){
    		exist = new MutableLong();
    		map.put( key, exist);
    	}
    	return exist;
    }
    
    /** JSP에서 EL을 사용하기 위한 용도 */
    @SuppressWarnings("unchecked")
	@Override
    public Long get(Object key) {
    	MutableLong exist = map.get(key);
    	if(exist==null){
    		exist = new MutableLong();
    		map.put((ID) key, exist);
    	}
    	return exist.toLong();
    }
    
    /** 간단로직을 위해 추가 */
    public long getValueOrTotalCount(ID key) {
        if(key==null) return totalCount();
        return get(key);
    }
    
    public long totalCount() {
        long sum = 0;
        for(MutableLong each : map.values()) sum += each.longValue();
        return sum;
    }
    
    public void clear() {
        map.clear();
    }

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Long put(ID key, Long value) {
		MutableLong exist = map.put(key, new MutableLong(value)); 
		return exist==null ? null : exist.toLong();
	}

	public Long remove(Object key) {
		MutableLong exist = map.remove(key);
		return exist==null ? null : exist.toLong();
	}

	public void putAll(Map<? extends ID, ? extends Long> other) {
		for(java.util.Map.Entry<? extends ID, ? extends Long> entry : other.entrySet()){
    		addAndGet(entry.getKey(), entry.getValue().longValue());
    	}
	}
	
	/** 동일한거 */
	public void putAllNumber(Map<? extends ID, ? extends Number> other) {
		for(java.util.Map.Entry<? extends ID, ? extends Number> entry : other.entrySet()){
    		addAndGet(entry.getKey(), entry.getValue().longValue());
    	}
	}

	public Set<ID> keySet() {
		return map.keySet();
	}

	public Collection<Long> values() {
		List<Long> values = Lists.newArrayList();
		for(Entry<ID, MutableLong> entry : map.entrySet()){
			values.add(entry.getValue().toLong());
    	}
		return values;
	}

	public Set<java.util.Map.Entry<ID, Long>> entrySet() {
		Map<ID,Long> entryMap = Maps.newHashMap();
		for(Entry<ID, MutableLong> entry : map.entrySet()){
			entryMap.put(entry.getKey(), entry.getValue().toLong());
    	}
		return entryMap.entrySet();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public Iterator<java.util.Map.Entry<ID, Long>> iterator() {
		return entrySet().iterator();
	}
    
    
	
    
}
