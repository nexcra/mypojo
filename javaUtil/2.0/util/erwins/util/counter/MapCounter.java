package erwins.util.counter;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 맵으로 숫자세기.  MultiSet이 int 단위밖에 안되서 대체 사용한다 (로그 카운트용)
 * @author sin
 */
public class MapCounter implements Iterable<Entry<String,AtomicLong>>,Serializable{
    
	private static final long serialVersionUID = 5436538814969364085L;
	
	private Map<String,AtomicLong> map = new ConcurrentHashMap<String, AtomicLong>();

    public long incrementAndGet(String key){
        AtomicLong value = get(key);
        return value.incrementAndGet();
    }
    
    public long addAndGet(String key,long delta){
        AtomicLong value = get(key);
        return value.addAndGet(delta);
    }

    /** null이 입력되면 안된다. */
    public synchronized AtomicLong get(String key) {
    	AtomicLong value = map.get(key);
        if(value==null){
            value = new AtomicLong();
            map.put(key, value);
        }
        return value;
    }
    
    public long getValue(String key) {
        return get(key).get();
    }
    
    /** 간단로직을 위해 추가 */
    public long getValueOrTotalCount(String key) {
        if(key==null) return totalCount();
        return getValue(key);
    }
    
    public synchronized long totalCount() {
        long sum = 0;
        for(AtomicLong each : map.values()) sum += each.get();
        return sum;
    }
    
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<Entry<String, AtomicLong>> iterator() {
        return map.entrySet().iterator();
    }

    /** 특수용도 */
	public Map<String, AtomicLong> getMap() {
		return map;
	}
    
    
}
