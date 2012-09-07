package erwins.util.webapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;


public abstract class MemCacheRepository<ID extends Serializable,T>{

	protected Cache cache;

	public void init(int expirationSec) throws CacheException {
		Map<Object, Object> config = new HashMap<Object, Object>();
		config.put(GCacheFactory.EXPIRATION_DELTA, expirationSec);
		config.put(MemcacheService.SetPolicy.SET_ALWAYS, Boolean.TRUE); //이거만 쓸듯 기본값임
		CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
		cache = cacheFactory.createCache(config);
	}

	@SuppressWarnings("unchecked")
	public T getData(ID id) {
		return (T)cache.get(id);
	}
	@SuppressWarnings("unchecked")
	public Map<ID,T> getDataAll(Collection<ID> id) {
		return (Map<ID,T>)cache.getAll(id);
	}
	
	private static final String KEY_SET = "keySet";
	
	@SuppressWarnings("unchecked")
	public void setData(ID key,T info) {
		cache.put(key, info);
	}
	
	/** 전체목록을 갱신하기 위해서 2가지로 분류 */
	@SuppressWarnings("unchecked")
	public void setDataAndUpdateList(ID key,T info) {
		cache.put(key, info);
		Set<ID> keySet = getKeySet();
		if(!keySet.contains(key)){
			keySet.add(key);
			cache.put(KEY_SET, keySet);
		}
		cache.remove(keySet);
	}
	
	/** 흠.. 여기서 걸리나? */
	@SuppressWarnings("unchecked")
	public List<T> getDatas() {
		Set<ID> keySet = getKeySet();
		List<T> result = new ArrayList<T>();
		Map<ID,T> map =  cache.getAll(keySet);
		for(Entry<ID,T> each :  map.entrySet()){
			T data = each.getValue();
			if(data==null) keySet.remove(each.getKey());
			else result.add(data);
		}
		if(keySet.size() != result.size()) cache.put(KEY_SET, keySet);
		/*
		List<T> result = new ArrayList<T>();
		Iterator<ID> i = keySet.iterator();
		while(i.hasNext()){
			ID key = i.next();
			T info = (T) cache.get(key);
			if(info==null) i.remove();
			else result.add(info);
		}
		if(keySet.size() != result.size()) cache.put(KEY_SET, keySet);
		*/
		return result;
	}

	@SuppressWarnings("unchecked")
	private Set<ID> getKeySet() {
		Set<ID> keySet = (Set<ID>) cache.get(KEY_SET);
		if(keySet==null) keySet = new HashSet<ID>();
		return keySet;
	}

	/** 서버의 재기동 후 캐시에 남아있을때 오류가능. */
	public void clear() {
		cache.clear();
	}

}
