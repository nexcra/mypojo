package erwins.util.tools.csvLog;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import erwins.util.spring.TransactionSynchCommit;
import erwins.util.spring.TransactionSynchCommit.AfterCompletionAble;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLog;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLogInfo;


/**
 * 로그를 쓰다가 WAS 인스턴스를 강제동료할경우, 기본적으로 버퍼 단위로 쓰기 때문에 line을 반쯤 쓰다가 멈추게 된다. 
 * 종료 플러시를 보장하지 못하는 일반적인 상황(WAS를 내릴때 프로세스를 KILL해버리는 경우)에서는 주기적으로 플러시를 해주자.   
 * 
 * 주의! 이 로그를 단순 로그가 아니라 자료로 사용하려면 반드시 트랜잭션 동기화 기능을 사용해야 한다.
 *  */
@Data
public class CsvLogWriter<T> {

	private final CsvLogInfo<T> csvLogInfo;
	private final BlockingQueue<CsvLog> queue;
	
	/** 정확하지 않아도 된다.. */
	private AtomicLong counter = new AtomicLong();
	private long limie = 1000L;
	private long timeoutSec = 2;
	
	/**  매 로그마다 플러시를 할지 여부. null일 경우 auto모드로 작동한다. */
	private Boolean flush;
	/** 트랜잭션이 성공할때만 작동한다. */
	private boolean withTransaction = false;
	
	public CsvLogWriter<T> setFlush(Boolean flush){
		this.flush = flush;
		return this;
	}
	public CsvLogWriter<T> setWithTransaction(boolean withTransaction){
		this.withTransaction = withTransaction;
		return this;
	}
	
	public void writeItem(Object vo){
		T item = csvLogInfo.getCsvLogConverter().convert(vo);
		writeLog(item);
	}
	
	/** 
	 * null을 입력하면 자동 롤링을 분리하려는 의도이다. (이벤트가 일어나야만 파일교체가 된다.)
	 *  */
	public void writeLog(T item){
		boolean currentFlush = false;
		if(flush==null){
			//지동으로 플러싱을 하는 모드
			long current = counter.incrementAndGet();
			if(current % limie == 0) currentFlush = true;
		}else{
			currentFlush = flush;
		}
		String[] data = item==null ? null : csvLogInfo.getCsvAggregator().aggregate(item);
		final CsvLog log = new CsvLog(csvLogInfo.getName(), data);
		if(currentFlush) log.setFlush(currentFlush);
		if(withTransaction){
			CsvLogWriterAfterCompletionAble able = TransactionSynchCommit.getResource(CsvLogWriterAfterCompletionAble.class);
			if(able==null){
				able = new CsvLogWriterAfterCompletionAble();
				TransactionSynchCommit.registerSynchronization(able);
			}
			able.add(log);
		}else{
			offerLog(log);	
		}
	}
	
	/** 로그를 모았다가 트랜잭션이 끝날때, 한번에 쓴다. */
	private class  CsvLogWriterAfterCompletionAble implements AfterCompletionAble{

		private List<CsvLog> list = Lists.newArrayList();
		
		/** 마지막 자료만 플러싱 하도록 최적화 할 수 있긴 하다. 난 귀찮아서 안했다. */
		@Override
		public void afterCompletionCommit() {
			for(CsvLog log : list){
				offerLog(log);
			}
		}

		@Override
		public void afterCompletionRollback() {
			//none
		}
		
		public void add(CsvLog e) {
			list.add(e);
		}
		
	}
	
	private void offerLog(final CsvLog log) {
		try {
			boolean success = queue.offer(log, timeoutSec, TimeUnit.SECONDS);
			if(!success) throw new IllegalStateException("queue.offer fail. check queue size");
		} catch (InterruptedException e) {
			Throwables.propagate(e);
		}
	}

}
