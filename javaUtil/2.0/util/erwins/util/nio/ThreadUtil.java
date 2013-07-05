package erwins.util.nio;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

}
