package erwins.util.vender.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import erwins.util.lib.ReflectionUtil;
import erwins.util.root.Pair;
import erwins.util.root.Pair.PairEnum;


/**
 * Pair이지만 Enum이 아닐경우 nullSafeGet()을 오버라이딩 하자.
 * 이 클래스는 모두가 불변 객체일때만 사용해야 한다.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class GenericPairUserType<T extends Pair> implements UserType{

	/** Enum이 아닐 경우 각 개싱 타입에 맞추어 요걸 오버라이딩 해야 한다. */
    public T nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if(rs.wasNull()) return null;
        return PairEnum.getEnum(this.returnedClass(), value);
    }

    /** 검색에 Code 대신 Key를 넘겨도 되도록 수정한다. */
    public void nullSafeSet(PreparedStatement pst, Object object, int index) throws HibernateException, SQLException {
    	if(object==null) pst.setString(index,null);
    	else if(object instanceof Pair) pst.setString(index,((Pair)object).getValue());
        else pst.setString(index,object.toString()); 
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

    @SuppressWarnings("unchecked")
	public Class<T> returnedClass() {
        return (Class<T>) ReflectionUtil.genericClass(this.getClass(), 0);
    }
	
}
