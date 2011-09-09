package erwins.util.exception;

import org.apache.commons.lang.Validate;

import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringUtil;

/**
 * Validation의 줄임말 입력값이 false이면 @see {@link BusinessException}을 던진다.
 */
public abstract class Check extends Validate{

	// private IllegalStateException e;
	// private IllegalArgumentException e;
	// private UnsupportedOperationException e;

	/**
	 * 사용자가 정확한 값을 입력했는가?
	 */
	public static void isPositive(Number count, String message) {
		if (count != null && count.doubleValue() > 0) return;
		throw new BusinessException(message);
	}

	/**
	 * 빈값이 아님.
	 */
	public static void isNotEmpty(Object obj, String message){
		if(ReflectionUtil.isEmpty(obj)) throw new BusinessException(message);
	}
	
	/**
	 * 두개가 같은지? 이건 test용으로만 사용
	 */
	public static void isEquals(Object a, Object b) {
		isTrue(a.equals(b));
	}
	
	public static void isNotEmpty(String str) {
		isTrue(StringUtil.isNotEmpty(str));
	}


	/** 예외를 던지는지 검사한다. expect는 nulll로 해도 된다. @ExpectedException를 대체한다. */
	public static <T extends Exception> void isThrowException(ExceptionRunnable runnable, Class<T> expect) {
		try {
			runnable.run();
		} catch (Exception e) {
			if (expect != null) {
				if (expect.isInstance(e)) return;
				if (expect.isInstance(e.getCause())) return;
			}
		}
		throw new RuntimeException("예상된 예외가 발생되지 않았습니다. " + expect == null ? "" : expect.getClass().getName());
	}

	public static void isThrowException(ExceptionRunnable runnable) {
		isThrowException(runnable, null);
	}

	public static interface ExceptionRunnable {
		public void run() throws Exception;
	}

}
