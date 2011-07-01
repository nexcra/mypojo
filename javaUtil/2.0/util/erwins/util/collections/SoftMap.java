package erwins.util.collections;

import java.lang.ref.SoftReference;
import java.util.HashMap;


/**
 * 메모리가 남아돌때 쓰세요.
 * JVM과 설정에 따라 다르겠지만.. 1초 정도 안쓰니까 슝 GC되버림.  이런게 있다 정도?
 */
public class SoftMap<ID,V>{

    private HashMap<ID,SoftReference<V>> cache = new HashMap<ID,SoftReference<V>>();
    
    private long hit;
    private long miss;
    
    public synchronized String hitRate(){
        return hit + "/" + (hit+miss) + " => hit rate is " + hit*1.0/(hit+miss)*100+"%";
    }

    public synchronized void put(ID key,V obj) {
        cache.put(key, new SoftReference<V>(obj));
    }

    public synchronized V get(ID key) {
        SoftReference<V> ref = cache.get(key);
        if(ref==null){
            miss++;
            return null;
        }
        V obj = ref.get();
        if(obj==null) miss++; 
        else hit++;
        return obj;
    }
    
    public synchronized V remove(ID key) {
        SoftReference<V> ref = cache.remove(key);
        if(ref==null) return null;
        return ref.get();
    }
    
}