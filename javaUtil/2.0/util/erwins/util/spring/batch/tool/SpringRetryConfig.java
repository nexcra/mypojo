package erwins.util.spring.batch.tool;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Data;

import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import erwins.util.root.Incompleted;
import erwins.util.root.exception.PropagatedRuntimeException;

/** 
 * 간단 리트라이 테스트기.  이름이 겹치면 다른걸로 바꾸자.
 * backoff 설정에 유의해야 한다. 2개의 스래드가 PK중복 오류로 backoff됬는데 동일시간을 sleep하다가 또 동시에 실행된다면 다시 PK중복이 일어날것이다. 
 * 
 * of()로 간단 생성
 * 신규 버전에서는 패키지 이동이 있다. 주의할것 
 * */
@Incompleted
public class SpringRetryConfig {
	
	/** PK중복의 경우 DuplicateKeyException 이런거 쓰면 됨요. 일단은 스프링 기본 */
	private Map<Class<? extends Throwable>,Boolean> retryableExceptions = Collections.<Class<? extends Throwable>, Boolean>singletonMap(Exception.class, true);
	/** default 3 */
	private Integer maxAttempts = 3;
	/** default 1 */
	private Integer backoffSec;
	/** backoff가 설정된 경우 최대 대기시간 */
	private Integer maxIntervalMin = 10;
	private boolean traverseCauses = false;
	 
	/** 간단 메소드 */
	public static SpringRetryConfig of(){
		return new SpringRetryConfig()
		.addRetryableExceptions(DuplicateKeyException.class).addRetryableExceptions(DeadlockLoserDataAccessException.class);
	}
	
	public SpringRetryConfig addRetryableExceptions(Class<? extends Throwable> clazz) {
		this.retryableExceptions.put(clazz, Boolean.TRUE);
		return this;
	}
	public SpringRetryConfig setBackoffSec(Integer backoffSec) {
		this.backoffSec = backoffSec;
		return this;
	}
	public SpringRetryConfig setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}
	public SpringRetryConfig setMaxIntervalMin(Integer maxIntervalMin) {
		this.maxIntervalMin = maxIntervalMin;
		return this;
	}
	/** 나중에 리커버리 가능하게 하나 더 만들자 */
	public <T,E extends Throwable> RetryResult<T> doWithRetry(RetryCallback<T,E> retryCallback){
		RetryTemplate template = new RetryTemplate();
		
		SimpleRetryPolicy policy = new SimpleRetryPolicy(maxAttempts,retryableExceptions,traverseCauses);
		template.setRetryPolicy(policy);
		
		if(backoffSec!=null){
			ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
			backOffPolicy.setMaxInterval(TimeUnit.MINUTES.toMillis(maxIntervalMin)); //최대 대기시간 
			backOffPolicy.setInitialInterval(TimeUnit.SECONDS.toMillis(backoffSec)); //기본 2배로 늘어난다.	
		}
		
		final RetryResult<T> retryResult = new RetryResult<T>();
		
		//단순히 RetryContext를 얻기 위한 장치이다.
		template.registerListener(new RetryListenerSupport() {
			@SuppressWarnings("hiding")
			public <T,E extends Throwable> boolean open(RetryContext context, RetryCallback<T,E> callback) {
				retryResult.setContext(context);
				return true;
			}
			@SuppressWarnings("hiding")
			public <T,E extends Throwable> void onError(RetryContext context,RetryCallback<T,E> callback, Throwable throwable) {
				String key = throwable.getClass().getSimpleName();
				synchronized (context) {
					Integer errorCount =  (Integer) context.getAttribute(key);
					if(errorCount==null) errorCount = 1;
					errorCount ++;
					context.setAttribute(key, errorCount);	
				}
				super.onError(context, callback, throwable);
			}
		});
		
		try {
			T result = template.execute(retryCallback); 
			retryResult.setResult(result);
			return retryResult;
		} catch (Throwable ex) {
			//if(e instanceof RuntimeException) throw (RuntimeException)e;
			throw new PropagatedRuntimeException("exception while trying.. " + retryResult.getContext(),ex);
		}
	}
	
	@Data(staticConstructor="of")
	public static class RetryResult<T>{
		private RetryContext context;
		private T result;
	}
	
	
	
	
	
}
