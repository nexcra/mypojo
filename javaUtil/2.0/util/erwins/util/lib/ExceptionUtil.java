package erwins.util.lib;

import org.apache.commons.lang.exception.ExceptionUtils;



/** 
 * 별 필요없다..  아래 클래스들 참조해서 없는거 추가.
 * org.apache.commons.lang.exception.ExceptionUtils
 * com.google.common.base.Throwables
 * 
 * Throwables.propagate(e); 요런것
 *   */
public abstract class ExceptionUtil extends ExceptionUtils{
	
	/**
	 * 하나라도 매칟되면 true를 리턴한다. 
	 * 무한루프 주의!
	 * CauseRetryPolicy와 호환되지 못한다. ㅠ
	 * 
	 * 이게 사용된다면 뭔가 잘못되었다는 의미이다. IO예외 등을 런타임으로 변경해주지 말고 새 정의를 사용하자.
	 */
	public static <T extends Throwable> boolean isAssignableFrom(Throwable ex,Class<T> clazzs) {
		if (ex == null) return false;
		Class<?> exClass = ex.getClass();
		if (clazzs.isAssignableFrom(exClass)) return true;
		
		Throwable cause = ex.getCause();
		while(cause != null){
			Class<?> causeClass = cause.getClass();
			if (clazzs.isAssignableFrom(causeClass)) return true;
			cause = cause.getCause();
		}
		
		return false;
	}
	
}
