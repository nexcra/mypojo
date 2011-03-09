package erwins.util.vender.hibernate;

import java.io.Serializable;
import java.sql.*;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.ParameterizedType;

import erwins.util.root.Pair;


/**
 * 범용 객체 매핑기.  캐싱 객체 사용시 오버라이딩해서 사용할것.
 * HQL에서 사용할 수는 없음.
 * @author erwins(my.pojo@gmail.com)
 */
public class SampleGenericCompositeUserType<T extends Pair> implements CompositeUserType ,ParameterizedType{

    public Object assemble(Serializable arg0, SessionImplementor arg1, Object arg2) throws HibernateException {
        return null;
    }

    public Object deepCopy(Object arg0) throws HibernateException {
        return null;
    }

    public Serializable disassemble(Object arg0, SessionImplementor arg1) throws HibernateException {
        return null;
    }

    public boolean equals(Object arg0, Object arg1) throws HibernateException {
        return false;
    }

    public String[] getPropertyNames() {
        return null;
    }

    public Type[] getPropertyTypes() {
        return null;
    }

    public Object getPropertyValue(Object arg0, int arg1) throws HibernateException {

        return null;
    }

    public int hashCode(Object arg0) throws HibernateException {

        return 0;
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet arg0, String[] arg1, SessionImplementor arg2, Object arg3) throws HibernateException, SQLException {
        return null;
    }

    public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2, SessionImplementor arg3) throws HibernateException, SQLException {
        
    }

    public Object replace(Object arg0, Object arg1, SessionImplementor arg2, Object arg3) throws HibernateException {
        return null;
    }

    @SuppressWarnings("rawtypes")
	public Class returnedClass() {
        return null;
    }

    public void setPropertyValue(Object arg0, int arg1, Object arg2) throws HibernateException {
        
    }

	@Override
	public void setParameterValues(Properties arg0) {
	}

	
}
