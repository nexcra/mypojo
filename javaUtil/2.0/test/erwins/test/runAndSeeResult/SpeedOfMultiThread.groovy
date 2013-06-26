package erwins.test.runAndSeeResult


import static org.junit.Assert.*

import java.util.concurrent.CyclicBarrier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import org.junit.Test

import erwins.util.tools.StopWatch

/** 멀티스래드에 대한 속도를 측정한다.. 조낸 쪼금 빨라지네 쩝..
 * 멀티코어에서 돌려보고싶당~ */
class SpeedOfMultiThread {
	
	@Test
	void test(){
		/** 입력수에 시간이 정비례하는 작업클로저를 정의한다. */
		
		def fn = { [1 .. it]*.inject(0){ sum,e -> sum += RandomStringUtil.makeRandomSid().toLong() }[0] }
		
		def c = 500
		def h = c / 2
		println StopWatch.load { fn(c) }
		
		CyclicBarrier barrier = new CyclicBarrier(3)
		ExecutorService pool = Executors.newFixedThreadPool(2)
		
		println StopWatch.load {
			pool.execute  { fn(h); barrier.await(); println 'a종료' }
			pool.execute  { fn(h); barrier.await(); println 'b종료' }
			barrier.await()
		}
		pool.shutdown()
	}
	

}
