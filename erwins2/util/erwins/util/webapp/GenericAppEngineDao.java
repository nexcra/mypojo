package erwins.util.webapp;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import javax.annotation.Resource;
import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.springframework.orm.jdo.JdoObjectRetrievalFailureException;
import org.springframework.orm.jdo.support.JdoDaoSupport;

import erwins.util.root.EntityId;

public abstract class GenericAppEngineDao<T extends EntityId<String>>  extends JdoDaoSupport{
	
	private Class<T> persistentClass;
    
    @SuppressWarnings("unchecked")
	public GenericAppEngineDao() {
        this.persistentClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    public Class<T> getPersistentClass() {
        return persistentClass;
    }
	
    /** persistenceManagerFactory라는 이름으로 bean이 등록되어 있어야 한다. */
	@Resource(name="persistenceManagerFactory")
    public void init(PersistenceManagerFactory persistenceManagerFactory){
		setPersistenceManagerFactory(persistenceManagerFactory);
    }
	
	public Collection<T> findAll(){
		return  getJdoTemplate().find(getPersistentClass());
	}
	protected Collection<T> findAll(String order){
		return  getJdoTemplate().find(getPersistentClass(),null,order);
	}
	/** 없으면 null을 리턴한다. */
	public T getById(String id){
		T entity;
		try {
			entity = getJdoTemplate().getObjectById(getPersistentClass(), id);
		} catch (JdoObjectRetrievalFailureException e) {
			if(e.getRootCause() instanceof NucleusObjectNotFoundException ) return null;
			throw e;
		} 
		return  entity;
	}
	public T saveOrUpdate(T entity) {
		return getJdoTemplate().makePersistent(entity);
	}
	public void delete(T entity) {
		getJdoTemplate().deletePersistent(entity);
	}
	public void delete(String id) {
		T entity = getJdoTemplate().getObjectById(getPersistentClass(), id);
		getJdoTemplate().deletePersistent(entity);
	}
	
}
