package erwins.util.collections;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class CacheLinkedMap<T extends Object>{

    private final static int MAX_CACHE_SIZE = 20;
    
    private static Map<String, Object> cache = new LinkedHashMap<String, Object>(MAX_CACHE_SIZE, 0.75f, true) {
        private static final long serialVersionUID = 1;

        @SuppressWarnings("unchecked")
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    public synchronized void putCache(String userId, T list) {
        cache.put(userId, list);
    }

    @SuppressWarnings("unchecked")
    public synchronized T getCache(String userId) {
        return  (T)cache.get(userId);
    }
    
}