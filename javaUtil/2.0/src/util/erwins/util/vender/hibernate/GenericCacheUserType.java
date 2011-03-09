package erwins.util.vender.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;

import erwins.util.lib.ReflectionUtil;
import erwins.util.root.EntityId;


/**
 * 1개의 컬럼이 1개의 객체(프로퍼티는 여러개일 수 있음)와 매핑될 경우 사용
 * HQL에서 사용할 수는 없음.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class GenericCacheUserType<T extends EntityId<ID>,ID extends Serializable> extends GenericRootUserType {
	
	/** Entity의 키가 숫자형이라도 DB저장시 String일 수 있다. 따라서 Object로 받아준다. */
	protected abstract T getCacheById(Object id);

	public T nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        Object id  = rs.getObject(names[0]);
        if(rs.wasNull()) return null;
        return getCacheById(id);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
    /** 검색에 EntityId 대신 Key를 넘겨도 되도록 수정한다. */
	public void nullSafeSet(PreparedStatement ps, Object object, int index) throws HibernateException, SQLException {
        if(object instanceof EntityId){
			EntityId<ID> entity = (EntityId)object;
            ps.setObject(index,entity.getId());
        }else ps.setObject(index,object);
    }

    @SuppressWarnings("unchecked")
	public Class<T> returnedClass() {
    	return (Class<T>) ReflectionUtil.genericClass(this.getClass(), 0);
    }

	
}
