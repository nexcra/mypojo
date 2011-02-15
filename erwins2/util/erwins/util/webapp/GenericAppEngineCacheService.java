package erwins.util.webapp;

import org.springframework.transaction.annotation.Transactional;

import erwins.util.root.EntityId;

/** 한번의 트랜잭션에서 단일 엔티티를 부를때는 1개만 가능하다. 
 * 따라서 캐싱을 다시 불러오는  getDao().get(id)은 컨트롤러에서만 호출될 수 있다.*/
public abstract class  GenericAppEngineCacheService<T extends EntityId<String>>{
	
	protected abstract GenericAppEngineCacheDao<T> getDao();
	
	/** 없으면 null을 리턴한다. */
	public T getById(String id){
		return getDao().getById(id);
	}
	@Transactional
	public T saveOrUpdate(T entity) {
		return getDao().saveOrUpdate(entity);
	}
	@Transactional
	public void delete(String id) {
		getDao().delete(id);
	}
	public T get(String id) {
		return getDao().get(id);
	}
	
}
