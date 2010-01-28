
package erwins.util.vender.spring;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.lib.Clazz;
import erwins.util.lib.Sets;
import erwins.util.tools.SearchMap;
import erwins.util.vender.hibernate.GenericHibernateDao;

/**
 * GenericDao와 함께 사용하는 GenericService 입니다.
 * dao는 여러개 필요햘때가 많은지라.. 한방에 모아서 사용합니다.
 * 나름 쓸만하구만..
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class GenericService<T,ID extends Serializable>{
    
    protected Log log = LogFactory.getLog(this.getClass()); 

    protected Class<T> persistentClass;

    /**
     * 프록시 객체의 경우 ClassCastException을 던진다. 걍 무시할것.
     * 물론 로그역시 남지 않음.
     */
    @SuppressWarnings("unchecked")
    public GenericService() {        
        try{
            this.persistentClass = (Class<T>)Clazz.genericClass(this.getClass(),0);
        }
        catch (java.lang.ClassCastException e){
            log.info(e.getMessage());
        }
    }
    
    protected abstract GenericHibernateDao<T,ID> getDao();
    protected SpringIBatisDao getIBatisDao(){
        return null;
    }
    

    /**
     * request에서 객체를 추출해서 삭제
     */    
    @Transactional
    public void delete(ID id){
        getDao().makeTransient(id);
    }
    
    @Transactional
    public void saveOrUpdate(T entity){
        getDao().makePersistent(entity);
    }
    
    /**
     * 전체 객체를 읽어옴.
     */    
    @Transactional(readOnly=true)
    public List<T> findAll(){
        return getDao().findAll();
    }
    
    /**
     * 단일 객체를 읽어옴.
     */    
    @Transactional(readOnly=true)
    public T findById(ID id){
        return getDao().getById(id);
    }
    
    /**
     * 여러 객체를 읽어옴.
     */    
    @Transactional(readOnly=true)
    public List<T> list(Criterion ... criterion){
        return getDao().findBy(criterion);
    }
    
    /**
     * 여러 객체를 읽어옴.
     */    
    @Transactional(readOnly=true)
    public T unique(Criterion ... criterion){
        return Sets.getResultUnique(getDao().findBy(criterion));
    }
    
    /**
     * iBatis를 이용해서 여러 객체를 읽어옴.
     */    
    @Transactional(readOnly=true)
    public void list(SearchMap map,String key){
        getIBatisDao().queryForList(map,key);
    }
    
}
