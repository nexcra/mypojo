package erwins.util.spring.batch.tool;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Data;

import org.springframework.batch.retry.RetryCallback;
import org.springframework.batch.retry.RetryContext;
import org.springframework.batch.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.batch.retry.listener.RetryListenerSupport;
import org.springframework.batch.retry.policy.SimpleRetryPolicy;
import org.springframework.batch.retry.support.RetryTemplate;

import com.google.common.collect.Maps;

/** 
 * 간단 리트라이 테스트기.  이름이 겹치면 다른걸로 바꾸자.
 * backoff 설정에 유의해야 한다. 2개의 스래드가 PK중복 오류로 backoff됬는데 동일시간을 sleep하다가 또 동시에 실행된다면 다시 PK중복이 일어날것이다. 
 * 
 * 신규 버전에서는 패키지 이동이 있다. 주의할것 
 * */
public class SpringRetryConfig {
	
	/** PK중복의 경우 DuplicateKeyException 이런거 쓰면 됨요 */
	private Map<Class<? extends Throwable>,Boolean> retryableExceptions = Maps.newHashMap();
	/** default 3 */
	private Integer maxAttempts;
	/** default 1 */
	private Integer backoffSec;
	/** backoff가 설정된 경우 최대 대기시간 */
	private Integer maxIntervalMin = 10;
	
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
	public <T> RetryResult<T> doWithRetry(RetryCallback<T> retryCallback){
		RetryTemplate template = new RetryTemplate();
		
		SimpleRetryPolicy policy = new SimpleRetryPolicy();
		if(!retryableExceptions.isEmpty()) policy.setRetryableExceptions(retryableExceptions);
		if(maxAttempts!=null) policy.setMaxAttempts(maxAttempts);
		template.setRetryPolicy(policy);
		
		if(backoffSec!=null){
			ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
			backOffPolicy.setMaxInterval(TimeUnit.MINUTES.toMillis(maxIntervalMin)); //최대 대기시간 
			backOffPolicy.setInitialInterval(TimeUnit.SECONDS.toMillis(backoffSec)); //기본 2배로 늘어난다.	
		}
		
		final RetryResult<T> retryResult = new RetryResult<T>();
		
		//단순히 RetryContext를 얻기 위한 장치이다.
		template.registerListener(new RetryListenerSupport() {
			@Override @SuppressWarnings("hiding")
			public <T> boolean open(RetryContext context, RetryCallback<T> callback) {
				retryResult.setContext(context);
				return true;
			}
			@Override @SuppressWarnings("hiding")
			public <T> void onError(RetryContext context,RetryCallback<T> callback, Throwable throwable) {
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
		} catch (Exception e) {
			if(e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException("exception while trying.. ",e);
		}
	}
	
	@Data(staticConstructor="of")
	public static class RetryResult<T>{
		private RetryContext context;
		private T result;
	}
	
	
	
	
	
}
