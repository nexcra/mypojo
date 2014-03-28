package erwins.util.tools.csvLog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;
import erwins.util.spring.batch.CsvItemWriter.CsvAggregator;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLog;


/**
 * 로그를 쓰다가 WAS 인스턴스를 강제동료할경우, 기본적으로 버퍼 단위로 쓰기 때문에 line을 반쯤 쓰다가 멈추게 된다. 
 * 종료 플러시를 보장하지 못하는 일반적인 상황(WAS를 내릴때 프로세스를 KILL해버리는 경우)에서는 주기적으로 플러시를 해주자.   
 *  */
@Data
public class CsvLogWriter<T> {

	private final String name;
	private final CsvAggregator<T> csvAggregator;
	private final BlockingQueue<CsvLog> queue;
	private AtomicLong counter = new AtomicLong();
	private long limie = 1000L;
	
	public void writeLog(T item) throws InterruptedException{
		long current = counter.incrementAndGet();
		if(current % limie == 0) writeLog(item,true);
		else writeLog(item,false);
	}
	
	/** 강제 플러싱을 포함하는 경우 */
	public void writeLog(T item,boolean flush) throws InterruptedException{
		String[] data = item==null ? null : csvAggregator.aggregate(item);
		CsvLog log = new CsvLog(name, data);
		if(flush) log.setFlush(flush);
		queue.put(log);
	}

}
