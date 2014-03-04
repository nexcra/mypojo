package erwins.util.nio;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Lists;

/** 뭔가 허접하다.. 쓸모없는듯 ㅠㅠ */
public abstract class ThreadUtil {

	/** 간이 테스트용 */
	public static void sleep(int interval) {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			// 무시한다.
		}
	}

	/** 간이 테스트 등에서 사용된다. */
	public static <T> List<Future<T>> call(int thred,Callable<T> callable) {
		ExecutorService es = Executors.newCachedThreadPool();
		List<Future<T>> list = Lists.newArrayList();
		for (int i = 0; i < thred; i++) {
			list.add(es.submit(callable));
		}
		return list;
	}
	
	/** 오래 걸리는 다수의 RMI를 동시에 실행할때 사용 */
	public static <T> List<Future<T>> call(Collection<Callable<T>> callables) {
		ExecutorService es = Executors.newCachedThreadPool();
		List<Future<T>> list = Lists.newArrayList();
		for (Callable<T> callable : callables) {
			list.add(es.submit(callable));
		}
		return list;
	}
	
	/** 간이 테스트 등에서 사용된다.  전부 기다려서 합을 리턴 */
	public static <T extends Number> Long sum(List<Future<T>> list) throws InterruptedException, ExecutionException {
		long sum = 0;
		for(Future<T> each : list){
			sum += each.get().longValue();
		}
		return sum;
	}
	
	/**  
	 * 간이 풀을 만들때 사용한다.
	 * setMaxPoolSize / setQueueCapacity 은 기본 Integer.MAX 이다. */
	public static ThreadPoolTaskExecutor defaultPool(int corePoolSize){
		ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(corePoolSize);
        ex.setKeepAliveSeconds(60);
        ex.setThreadNamePrefix("defaultPool_");
        ex.afterPropertiesSet();
        return ex;
	}

}
