package erwins.jsample.current;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * CycleBarrier => 사이클마다 여러 스래드의 스타트를 제어하는 배리어를 생성 및 제거. 
	각 스래드는 먼저 도착하더라도 배리어가 풀려야 진행 가능. 단계별 배리어 액션 실행 가능.
	시뮬레이션 알고리즘 등에서 사용.
	CountDownLatch와는 달리 이벤트가 아닌 다른 스래드를 기다린다.
 * put과 get의 합계가 정확한지 비교한다.
 */
public class PutTakeTestByBarrier{

    @Test
    public void run() throws Exception {
        PutTake pt = new PutTake(10, 10, 100000); // sample parameters
        pt.test();
        PutTake.pool.shutdown();
    }
    
    static class PutTake{
        protected static final ExecutorService pool = Executors.newCachedThreadPool();
        protected CyclicBarrier barrier;
        protected final SemaphoreBoundedBuffer<Integer> bb;
        protected final int nTrials, nPairs;
        protected final AtomicInteger putSum = new AtomicInteger(0);
        protected final AtomicInteger takeSum = new AtomicInteger(0);

        public PutTake(int capacity, int npairs, int ntrials) {
            this.bb = new SemaphoreBoundedBuffer<Integer>(capacity);
            this.nTrials = ntrials;
            this.nPairs = npairs;
            this.barrier = new CyclicBarrier(npairs * 2 + 1);
        }

        void test() {
            try {
                for (int i = 0; i < nPairs; i++) {
                    pool.execute(new Producer());
                    pool.execute(new Consumer());
                }
                barrier.await(); // wait for all threads to be ready
                barrier.await(); // wait for all threads to finish
                Assert.assertEquals(putSum.get(), takeSum.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        static int xorShift(int y) {
            y ^= (y << 6);
            y ^= (y >>> 21);
            y ^= (y << 7);
            return y;
        }
        
        class Producer implements Runnable {
            public void run() {
                try {
                    int seed = (this.hashCode() ^ (int) System.nanoTime());
                    int sum = 0;
                    barrier.await();
                    for (int i = nTrials; i > 0; --i) {
                        bb.put(seed);
                        sum += seed;
                        seed = xorShift(seed);
                    }
                    putSum.getAndAdd(sum);
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        class Consumer implements Runnable {
            public void run() {
                try {
                    barrier.await();
                    int sum = 0;
                    for (int i = nTrials; i > 0; --i) {
                        sum += bb.take();
                    }
                    takeSum.getAndAdd(sum);
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    


}
