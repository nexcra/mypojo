package erwins.util.root.exception;



/** 스래드에서 예외를 컨트롤 하려고 만들었다. */
public interface ExceptionHandler{

	/** 예외를 무시하고싶지 않다면 throw해주던가 하자.  */
	public void handleException(Throwable e);
	
}