package erwins.util.spring.batch.component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 *  Queue에는 병렬로 데이터가 입력된다. 
 *  1. Queue에서 commitInterval만큼의 데이터를 꺼냈거나
 *  2. Queue에 데이터가 없어서 timeout만큼 대기했다면
 *  itemWriter를 작동시킨다. 
 *  많은 요청을 단일 스래드로 배치 처리할때 주로 사용된다. (불특정 다수 로그의 DB입력 등)
 *  
 *  물론 WAS가 강제종료 되는경우 메모리에있던 값이 전부 소실됨으로 주의!
 *  
 *  만들긴 했는데 로깅용으론 부적합. 파일 롤링이 없음.. ㅠ
 *  일단 만들었지만 쓰는데는 없당~
 *  */
@Data
public class QueueWriter<T> implements Runnable{
	
	private final BlockingQueue<T> queue;
	private final ItemWriter<T> itemWriter;
	private TimeUnit unit = TimeUnit.SECONDS;
	private int timeout = 2;
	private int commitInterval = 1000;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		List<T> items = Lists.newArrayList();
		log.info(currentThread.getName() + " thread start");
		try {
			while(!currentThread.isInterrupted()){
				T item = queue.poll(timeout, unit);
				if(item==null){
					//타임아웃이 된 경우
					doItemWrite(items);
				}else{
					items.add(item);
					if(items.size() >= commitInterval ){
						doItemWrite(items);
					}
				}
			}
		} catch (InterruptedException e) {
			//마지막 남은 큐를 처리하고 죽는다.
			log.warn(this.getClass().getSimpleName() + " Interrupted! remain queue size : " + queue.size());
			doItemWrite(items);
			currentThread.interrupt(); //혹시나. while밖에 있어서 안해도 끝나긴 한다.
		}
		log.info(currentThread.getName() + " thread end");
	}

	protected void doItemWrite(List<T> items){
		try {
			if(items.size() > 0 ){
				itemWriter.write(items);
				items.clear();
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
	}

}
