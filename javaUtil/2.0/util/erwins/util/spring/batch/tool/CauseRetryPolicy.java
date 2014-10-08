package erwins.util.spring.batch.tool;

import java.util.Collections;
import java.util.Map;

import org.springframework.batch.classify.BinaryExceptionClassifier;
import org.springframework.batch.retry.RetryContext;
import org.springframework.batch.retry.RetryPolicy;
import org.springframework.batch.retry.context.RetryContextSupport;
import org.springframework.batch.retry.policy.SimpleRetryPolicy;

/**
 * cause를 사용하기 위해서 만든 새로운 리트라이정책. 
 * 기존 소스가 private 이라서 확장하지 못하고 복붙했다. 
 * */
public class CauseRetryPolicy implements RetryPolicy{

	/**
	 * The default limit to the number of attempts for a new policy.
	 */
	public final static int DEFAULT_MAX_ATTEMPTS = 3;

	private volatile int maxAttempts;

	private volatile BinaryExceptionClassifier retryableClassifier = new BinaryExceptionClassifier(false);

	/**
	 * Create a {@link SimpleRetryPolicy} with the default number of retry
	 * attempts.
	 */
	public CauseRetryPolicy() {
		this(DEFAULT_MAX_ATTEMPTS, Collections
				.<Class<? extends Throwable>, Boolean> singletonMap(Exception.class, true));
	}

	/**
	 * Create a {@link SimpleRetryPolicy} with the specified number of retry
	 * attempts.
	 * 
	 * @param maxAttempts
	 * @param retryableExceptions
	 */
	public CauseRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
		super();
		this.maxAttempts = maxAttempts;
		this.retryableClassifier = new BinaryExceptionClassifier(retryableExceptions);
	}

	/**
	 * @param retryableExceptions
	 */
	public void setRetryableExceptions(Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
		this.retryableClassifier = new BinaryExceptionClassifier(retryableExceptions);
	}

	/**
	 * Setter for retry attempts.
	 * 
	 * @param retryAttempts the number of attempts before a retry becomes
	 * impossible.
	 */
	public void setMaxAttempts(int retryAttempts) {
		this.maxAttempts = retryAttempts;
	}
	
	/**
	 * The maximum number of retry attempts before failure.
	 * 
	 * @return the maximum number of attempts
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * Test for retryable operation based on the status.
	 * 
	 * @see org.springframework.batch.retry.RetryPolicy#canRetry(org.springframework.batch.retry.RetryContext)
	 * 
	 * @return true if the last exception was retryable and the number of
	 * attempts so far is less than the limit.
	 */
	public boolean canRetry(RetryContext context) {
		Throwable t = context.getLastThrowable();
		return (t == null || retryForException(t)) && context.getRetryCount() < maxAttempts;
	}

	/**
	 * @see org.springframework.batch.retry.RetryPolicy#close(RetryContext)
	 */
	public void close(RetryContext status) {
	}

	/**
	 * Update the status with another attempted retry and the latest exception.
	 * 
	 * @see RetryPolicy#registerThrowable(RetryContext, Throwable)
	 */
	public void registerThrowable(RetryContext context, Throwable throwable) {
		SimpleRetryContext simpleContext = ((SimpleRetryContext) context);
		simpleContext.registerThrowable(throwable);
	}

	/**
	 * Get a status object that can be used to track the current operation
	 * according to this policy. Has to be aware of the latest exception and the
	 * number of attempts.
	 * 
	 * @see org.springframework.batch.retry.RetryPolicy#open(RetryContext)
	 */
	public RetryContext open(RetryContext parent) {
		return new SimpleRetryContext(parent);
	}

	@SuppressWarnings("serial")
	private static class SimpleRetryContext extends RetryContextSupport {
		public SimpleRetryContext(RetryContext parent) {
			super(parent);
		}
	}

	/**
	 * Delegates to an exception classifier.
	 * 
	 * @param ex
	 * @return true if this exception or its ancestors have been registered as
	 * retryable.
	 * 
	 * 이 부분이 변경됨. 
	 * 예외사유중 하나라도 일치하면 true -> 대부분 런타임 예외로 감싼것을 찾기 위해서 사용된다.
	 * 무한히 돌지는 않겠지? ㅎㅎ
	 */
	private boolean retryForException(Throwable ex) {
		//return retryableClassifier.classify(ex);
		
		if(retryableClassifier.classify(ex)) return true;
		
		Throwable cause = ex.getCause();
		while(cause != null){
			if(retryableClassifier.classify(cause)) return true;
			cause = cause.getCause();
		}
		return false;
		
	}
	
	
	
}
