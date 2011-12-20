
package erwins.jsample.current;

import java.util.concurrent.*;

/**
 * join의 대용으로 특정 이벤트( X회수만큼 액션이 일어나는것)가 발생할 때 까지 여러 스래드를 대기상태로 잡아둘 수 있다.
 */
public class CountDownLatchTest {
    public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        startGate.await();
                        try {
                            task.run();
                        }
                        finally {
                            endGate.countDown();
                        }
                    }
                    catch (InterruptedException ignored) {}
                }
            };
            t.start();
        }

        long start = System.nanoTime();
        startGate.countDown();
        endGate.await();
        long end = System.nanoTime();
        return end - start;
    }
}
