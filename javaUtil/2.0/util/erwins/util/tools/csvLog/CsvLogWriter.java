package erwins.util.tools.csvLog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;
import erwins.util.lib.ExceptionUtil;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLog;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLogInfo;


/**
 * 로그를 쓰다가 WAS 인스턴스를 강제동료할경우, 기본적으로 버퍼 단위로 쓰기 때문에 line을 반쯤 쓰다가 멈추게 된다. 
 * 종료 플러시를 보장하지 못하는 일반적인 상황(WAS를 내릴때 프로세스를 KILL해버리는 경우)에서는 주기적으로 플러시를 해주자.   
 *  */
@Data
public class CsvLogWriter<T> {

	private final CsvLogInfo<T> csvLogInfo;
	private final BlockingQueue<CsvLog> queue;
	
	/** 정확하지 않아도 된다.. */
	private AtomicLong counter = new AtomicLong();
	private long limie = 1000L;
	private long timeoutSec = 2;
	
	public void writeItem(Object vo){
		T item = csvLogInfo.getCsvLogConverter().convert(vo);
		writeLog(item);
	}
	
	public void writeItem(Object vo,boolean flush){
		T item = csvLogInfo.getCsvLogConverter().convert(vo);
		writeLog(item,flush);
	}
	
	/** 자동으로 플러싱을 하는 모드 */
	public void writeLog(T item){
		long current = counter.incrementAndGet();
		if(current % limie == 0) writeLog(item,true);
		else writeLog(item,false);
	}
	
	/** 강제 플러싱을 포함하는 경우  */
	public void writeLog(T item,boolean flush){
		String[] data = item==null ? null : csvLogInfo.getCsvAggregator().aggregate(item);
		CsvLog log = new CsvLog(csvLogInfo.getName(), data);
		if(flush) log.setFlush(flush);
		try {
			boolean success = queue.offer(log, timeoutSec, TimeUnit.SECONDS);
			if(!success) throw new RuntimeException("queue.offer fail. check queue size");
		} catch (InterruptedException e) {
			ExceptionUtil.throwException(e);
		}
	}

}
