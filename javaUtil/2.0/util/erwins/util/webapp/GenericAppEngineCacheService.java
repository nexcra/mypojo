package erwins.util.webapp;

import erwins.util.root.EntityId;

/** 한번의 트랜잭션에서 단일 엔티티를 부를때는 1개만 가능하다. 
 * 따라서 캐싱을 다시 불러오는  getDao().get(id)은 컨트롤러에서만 호출될 수 있다.*/
public abstract class  GenericAppEngineCacheService<T extends EntityId<String>> extends GenericAppEngineService<T>{
	
	protected abstract GenericAppEngineCacheDao<T> getDao();
	
	public T get(String id) {
		return getDao().get(id);
	}
	
}
