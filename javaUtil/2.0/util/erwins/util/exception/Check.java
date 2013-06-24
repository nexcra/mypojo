package erwins.util.exception;

import erwins.util.lib.ReflectionUtil;

/**
 * Check의 줄임말 입력값이 false이면 @see {@link BusinessException}을 던진다.
 * Preconditions로 변경하자
 */
@Deprecated
public abstract class Check{

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
	public static void isNotEmpty(Object obj){
		isNotEmpty(obj,"object is empty : " + obj);
	}
	
	public static void isTrue(boolean tf,String msg) {
		if(!tf) throw new BusinessException(msg);
	}
	public static void isTrue(boolean tf) {
		isTrue(tf,"is not true");
	}

	public static void isEquals(Object a, Object b,String msg) {
		isTrue(a.equals(b),msg);
	}
	public static void isEquals(Object a, Object b) {
		isEquals(a,b,"is not same object!");
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
