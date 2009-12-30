package erwins.util.valueObject;


/** 값객체를 표현한다. */
public interface ValueObject{
	
	/** 초기화. */
	public void setValue(Object obj);
	/** DB에 저장될 값을 리턴한다. */
	public Object getValue();
    
    
}