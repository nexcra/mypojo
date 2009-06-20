package erwins.util.vender.hibernate;

import java.io.Serializable;
import java.sql.*;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import erwins.util.valueObject.Won;


/**
 * @author erwins(my.pojo@gmail.com)
 */
public class MyCompositeUserType implements CompositeUserType {

    public Object assemble(Serializable arg0, SessionImplementor arg1, Object arg2) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object deepCopy(Object arg0) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    public Serializable disassemble(Object arg0, SessionImplementor arg1) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean equals(Object arg0, Object arg1) throws HibernateException {
        // TODO Auto-generated method stub
        return false;
    }

    public String[] getPropertyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public Type[] getPropertyTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getPropertyValue(Object arg0, int arg1) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    public int hashCode(Object arg0) throws HibernateException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isMutable() {
        // TODO Auto-generated method stub
        return false;
    }

    public Object nullSafeGet(ResultSet arg0, String[] arg1, SessionImplementor arg2, Object arg3) throws HibernateException, SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2, SessionImplementor arg3) throws HibernateException, SQLException {
        // TODO Auto-generated method stub
        
    }

    public Object replace(Object arg0, Object arg1, SessionImplementor arg2, Object arg3) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    public Class returnedClass() {
        return Won.class;
    }

    public void setPropertyValue(Object arg0, int arg1, Object arg2) throws HibernateException {
        // TODO Auto-generated method stub
        
    }

	
}
