package erwins.util.collections;

import java.io.Serializable;

import org.apache.commons.collections.map.LRUMap;

import erwins.util.root.EntityId;
import erwins.util.root.FetcherForId;

/**
 * 젤 많이 쓴거 말고 다 지운다. 쓸만하다!
 */
public class LRUCacheEngine<ID extends Serializable,T extends EntityId<ID>> {

	private final LRUMap map;
	
	private FetcherForId<ID,T> fetcherForId;
	private int hit;
	private int fail;
	private int totalAccess;

	/** 일반적으로 dao를 품는 FetcherForId를 넣어준다. */
	public LRUCacheEngine(int size,FetcherForId<ID,T> fetcherForId) {
		if (size < 1) throw new IllegalArgumentException("size < 1");
		map = new LRUMap(size);
		this.fetcherForId = fetcherForId;
	}

	/**
	 * 캐시로부터 객체를 구해온다.
	 * 페쳐를 거쳤는데도 못찾는다면 null을 리턴한다. (아마 오류일것이다)
	 */
	@SuppressWarnings("unchecked")
	public Object get(ID id){
		totalAccess++;
		T entity = (T)map.get(id);
		if (entity == null) {
			fail++;
			entity = fetcherForId.getById(id);
			if(entity==null) return null;
			map.put(id, entity);
		} else {
			hit++;
		}
		return entity;
	}

	public int getTotalAccess() {
		return totalAccess;
	}

	public int getHit() {
		return hit;
	}

	public int getFail() {
		return fail;
	}

}