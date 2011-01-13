package erwins.util.valueObject;


/**
 * ValueObject 생성 실패시 던진다.
 */
@SuppressWarnings("serial")
public class ValueObjectBuildException extends RuntimeException {

	private final Object org;
	
	public ValueObjectBuildException(Object org,String message) {
		super(message);
		this.org = org;
	}

	public Object getOrg() {
		return org;
	}
	

}