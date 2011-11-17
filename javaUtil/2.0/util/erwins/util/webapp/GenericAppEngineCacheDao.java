package erwins.util.webapp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import erwins.util.root.EntityId;

public abstract class GenericAppEngineCacheDao<T extends EntityId<String>>  extends GenericAppEngineDao<T>{
	
	private final Cache cache;
	
	public GenericAppEngineCacheDao(){
		super();
		Map<Object,Object> config = new HashMap<Object,Object>();
		config.put(GCacheFactory.EXPIRATION_DELTA,TimeUnit.HOURS.toSeconds(1));
		config.put(MemcacheService.SetPolicy.SET_ALWAYS,Boolean.TRUE);
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(config);
		} catch (CacheException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public T saveOrUpdate(T entity) {
		T t = super.saveOrUpdate(entity);
		put(t);
		return t;
	}
	
	@SuppressWarnings("unchecked")
	private void put(T entity){
		cache.put(entity.getId(),entity);
	}
	
	/** 없을때만 스토어를 조회한다. 캐시에도 없고 DB에도 없다면 null을 리턴한다.
	 * 한번의 트랜잭션에서 단일 엔티티를 부를때는 1개만 가능하다. 
	 * 따라서 캐싱을 다시 불러오는  getDao().get(id)은 컨트롤러에서만 호출될 수 있다.*/
	@SuppressWarnings("unchecked")
	public T get(String id){
		T entity = (T)cache.get(id);
		if(entity==null){
			entity = getById(id);
			put(entity);
		}
		return entity;
	}
	
	/** 서버의 재기동 후 캐시에 남아있을때 오류가능.  */
	public void clear(){
		cache.clear();
	}
	
}
