
package erwins.util.tools;

import java.io.Serializable;

import erwins.util.collections.SoftMap;
import erwins.util.root.Singleton;
import erwins.util.vender.hibernate.GenericHibernateDao;


/**
 * SoftMap을 구현하는 
 * @author  erwins(my.pojo@gmail.com)
 */
@Singleton
public class SoftMapDao<ID extends Serializable,T>{
    
    private GenericHibernateDao<T, ID> dao;
    private SoftMap<ID,T> softMap = new SoftMap<ID,T>();
    
    public SoftMapDao(GenericHibernateDao<T, ID> dao){
        this.dao = dao;
    }
    
    /**
     * 캐싱해서 가져옵니다. 
     */
    public T get(ID id) {
        if(id==null) return null;
         
        T obj = softMap.get(id);
        if(obj==null){
            obj = dao.getById(id);
            softMap.put(id, obj);
        }
        return obj;
    }
    
    /**
     * softMap을 삭제 후 새로 생성한다. 
     * 물론 T가 다른곳에서 참조되고 있으면 안된다.
     */
    public void deleteCache() {
        softMap = null;
        softMap = new SoftMap<ID,T>();
    }
    

}
