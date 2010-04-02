package erwins.util.vender.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;

import erwins.util.lib.Clazz;
import erwins.util.valueObject.ValueObject;

public abstract class GenericValueObjectUserType<T extends ValueObject> extends GenericRootUserType {

	public T nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		//if(rs.wasNull()) return null; //이거 뭔지??
		Object value = rs.getObject(names[0]);
		if(value==null) return null;
		T instance = Clazz.instance(returnedClass());
		instance.initValue(value);
        return  instance;
    }

    /** 검색에 Code 대신 Key를 넘겨도 되도록 수정한다. */
    public void nullSafeSet(PreparedStatement pst, Object object, int index) throws HibernateException, SQLException {
    	if(object==null) pst.setString(index,null);
    	else if(object instanceof ValueObject){
    		ValueObject valueObject = (ValueObject)object;
            pst.setObject(index,valueObject.returnValue());
        }else pst.setObject(index,object); 
    }

    @SuppressWarnings("unchecked")
	public Class<T> returnedClass() {
    	return (Class<T>) Clazz.genericClass(this.getClass(), 0);
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public Object deepCopy(Object value) throws HibernateException {
    	if(value==null) return null;
    	T instance = Clazz.instance(returnedClass());
    	if(value instanceof ValueObject) instance.initValue(((T)value).returnValue());
    	else instance.initValue(value);
        return instance;
    }

	
}
