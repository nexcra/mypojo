package erwins.util.hibernate;

import java.io.Serializable;


/** 값객체를 표현한다. equals를 반드시 정확히 오버라이딩 해야 하며 리플렉션을 사용함으로 getClass()는 금물이다. 
 * Serializable는 필수이다. */
public interface ValueObject extends Serializable{
	
	/** 초기화. */
	public void initValue(Object obj);
	
	/** DB에 저장될 값을 리턴한다. */
	public Object returnValue();
    
    
}