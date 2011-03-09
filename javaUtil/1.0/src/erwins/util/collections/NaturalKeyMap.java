package erwins.util.collections;

import java.util.HashMap;
import java.util.Map;


/**
 * 자연키 기반 캐싱할때 사용. 요건 ,GC되지 않음으로 수동으로 제거해야 한다. 
 * @author erwins(my.pojo@gmail.com)
 */
public class NaturalKeyMap<ID,KEY>{

    private Map<ID,KEY> cache = new HashMap<ID,KEY>();
    
    private long hit;
    private long miss;
    
    public long getHit() {
		return hit;
	}

	public long getMiss() {
		return miss;
	}

	public double hitRate(){
        return Math.round(hit*1.0/(hit+miss)*100*100)/100 ;
    }
	public String hitRateStr(){
		return hit + "/" + (hit+miss) + " => " + hitRate() +"%";
	}

    public synchronized void put(ID key,KEY obj) {
        cache.put(key,obj);
    }

    public synchronized KEY get(ID key) {
        KEY ref = cache.get(key);
        if(ref==null){
            miss++;
            return null;
        }
        hit++;
        return ref;
    }
    
    public synchronized void clear() {
    	cache = null;
    	cache = new HashMap<ID,KEY>();
    }
    
}