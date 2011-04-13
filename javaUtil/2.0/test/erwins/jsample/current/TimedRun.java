
package erwins.jsample.current;

import java.util.concurrent.*;

import erwins.util.exception.ExceptionUtil;

/**
 * Future 를 사용해 간단히 타임아웃이 걸린 스래드를 작동 가능하게 한다.
 * 시간이 지나면 자동으로 정지한다.
 */
public class TimedRun {
    private static final ExecutorService taskExec = Executors.newCachedThreadPool();

    public static void timedRun(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout, unit);
        }
        catch (TimeoutException e) {
            // task will be cancelled below
        }
        catch (ExecutionException e) {
            // exception thrown in task; rethrow
            ExceptionUtil.castToRuntimeException(e);
        }
        finally {
            // Harmless if task already completed
            //리턴값은 실제로 인터럽트가 결렸는가?
            task.cancel(true); // interrupt if running 
        }
    }
}
