package erwins.util.vender.hibernate;

import java.io.Serializable;
import java.sql.*;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;


/**
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class GenericUserType<T> implements UserType {

    public T nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String value  = rs.getString(names[0]);
        if(rs.wasNull()) return null;
        
        return null;
    }

    public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2) throws HibernateException, SQLException {
        // TODO Auto-generated method stub
    }
    
    /**
     * Enum매핑시 deepCopy가 필요 없다.
     */
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * 캐싱된 자료를 가져옴
     */
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }
    
    /**
     * Serializable하게 바꿔서 캐싱함.
     */
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable)deepCopy(value);
    }

    public boolean isMutable() {
        return true;
    }

    /**
     * DB에 값을 덮어쓸때(update) 사용한다.
     * 만약 특정 부분은 바껴도 업데이트 하지 않을려면 적당히 수정해준다.
     * @param original 준영속 상태의 프로퍼티 값.
     * @param target 데이터베이스에서 로딩한 프로퍼티 값.
     */
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    /**
     * 오직 문자열만을 저장한다.
     */
    public int[] sqlTypes() {
        return new int[] {Hibernate.STRING.sqlType()};
    }

    public boolean equals(Object a, Object b) throws HibernateException {
        if(a==b) return true;
        if(a==null || b==null) return false;
        return a.equals(b);
    }

    public int hashCode(Object obj) throws HibernateException {
        return obj.hashCode();
    }

	
}
