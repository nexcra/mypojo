package erwins.util.counter;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Maps;

import erwins.util.collections.AbstractMapSupport;

/**
 * 맵으로 숫자세기.  MultiSet이 int 단위밖에 안되서 대체 사용한다 (로그 카운트용)
 * @author sin
 */
public class MapIdCounter<ID> extends AbstractMapSupport<ID,Long> implements Iterable<Entry<ID,AtomicLong>>,Serializable {
    
	private static final long serialVersionUID = 5436538814969364085L;
	
	private Map<ID,AtomicLong> map = new ConcurrentHashMap<ID, AtomicLong>();

    public long incrementAndGet(ID key){
        AtomicLong value = getSynch(key);
        return value.incrementAndGet();
    }
    
    public long addAndGet(ID key,long delta){
        AtomicLong value = getSynch(key);
        return value.addAndGet(delta);
    }
    
    /** JSP에서 EL을 사용하기 위한 용도 */
    @SuppressWarnings("unchecked")
	@Override
    public Long get(Object key) {
    	return getSynch((ID) key).get();
    }

    /** null이 입력되면 안된다. */
    public synchronized AtomicLong getSynch(ID key) {
    	AtomicLong value = map.get(key);
        if(value==null){
            value = new AtomicLong();
            map.put(key, value);
        }
        return value;
    }
    
    /** 간단로직을 위해 추가 */
    public long getValueOrTotalCount(ID key) {
        if(key==null) return totalCount();
        return get(key);
    }
    
    public synchronized long totalCount() {
        long sum = 0;
        for(AtomicLong each : map.values()) sum += each.get();
        return sum;
    }
    
    public void clear() {
        map.clear();
    }
    
    public void putAll(MapIdCounter<ID> other){
    	for(Entry<ID, AtomicLong> entry : other){
    		addAndGet(entry.getKey(), entry.getValue().longValue());
    	}
    }
    
    /** other값을 현재 객체에 입력 후 other는 리셋한다. 
     * 주기적으로 값을 초기화 하는 로직에 적합하다. */
    public void putAllAndReset(MapIdCounter<ID> other){
    	for(Entry<ID, AtomicLong> entry : other){
    		addAndGet(entry.getKey(), entry.getValue().getAndSet(0L));
    	}
    }

    @Override
    public Iterator<Entry<ID, AtomicLong>> iterator() {
        return map.entrySet().iterator();
    }

    /** 특수용도 */
	public Map<ID, AtomicLong> getMap() {
		return map;
	}
	
	/** 스래드 안전하지 않다. 변환용 */
	@Override
	public Set<Entry<ID, Long>> entrySet() {
		Map<ID,Long> entryMap = Maps.newHashMap();
		for(Entry<ID, AtomicLong> entry : map.entrySet()){
			entryMap.put(entry.getKey(), entry.getValue().longValue());
    	}
		return entryMap.entrySet();
	}
	
    
}
