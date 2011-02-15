package erwins.util.webapp;

import org.springframework.transaction.annotation.Transactional;

import erwins.util.root.EntityId;


public abstract class  GenericAppEngineService<T extends EntityId<String>>{
	
	protected abstract GenericAppEngineDao<T> getDao();
	
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
	
}
