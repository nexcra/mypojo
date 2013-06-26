package erwins.util.hibernate;

import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;


/**
 * 1개의 컬럼이 1개의 객체(프로퍼티는 여러개일 수 있음)와 매핑될 경우 사용
 * 조건에 따라 HQL에서 사용할 수는 없음.
 */
public abstract class GenericRootUserType implements UserType {
	
    /** Enum매핑시에는 deepCopy가 필요 없다. 
     * 하이버네이트는 1차 캐싱된 객체를 비교해 dirty체크를 함으로 저장객체가 불변객체가 아니라면 반드시 오버라딩/구현을 해주어야 한다.
     * 뿐만 아니라 대상이 되는 객체의 equals를 정확히 기술해 주저야 한다. (리플렉션을 사용함으로 getClass()는 금물!)
     * 만약 검색 입력 등에서 기본타입 이외에 문자/숫자 등을 허용했다면 deepCopy에서도 이를 반영해 주어야 한다.
     *  */
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /** 캐싱된 자료를 가져옴 */
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
     * 오직 문자열만을 저장한다. 문자열이 아니라면 오버라이딩 해주어야 한다.
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
