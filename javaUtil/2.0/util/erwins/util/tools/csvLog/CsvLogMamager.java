package erwins.util.tools.csvLog;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

import com.google.common.base.Preconditions;

import erwins.util.root.exception.ExceptionHandler;


/** 
 * 이 클래스는 로그를 쓰는 스래드와, 로그 설정파일간(로거)의  queue를 연결해주는 기능을 한다.
 * 하나의 매니저와 1개의 별도 스래드로, 병렬환경에서 다수설정된 CSV로그를 남기기 위해서 사용한다.
 * X초당, X분당 등 자유로운 설정이 가능하다.
 * 일반적인 상황에서의 성능은 문제없다. 로컬노트북 1500만 로우(422M) 쓰기시 1분 47초. 초당 140186
 * ex) 
 * final CsvLogMamager manager = new CsvLogMamager();
		manager.add(new CsvLogInfo<Ad>("ad1",adDir ,agg).setType(DateTimeFieldType.minuteOfDay(), 1, CsvLogMamager.DEFAULT_TIME_PATTERN));
		manager.add());
		manager.startup();
		
	네이밍 샘플
	CsvLogRegister.java
	FileMonitorRegister.java
	ItemCrawlingMonitor.java
		
 *  */
@Data
public class CsvLogMamager {

	private final Map<String,CsvLogInfo<?>> csvLogInfoMap = new ConcurrentHashMap<String, CsvLogInfo<?>>();
	private BlockingQueue<CsvLog> queue;
	private CsvLogThread thread;
	/** 자료를 쓰는중 예외가 발생할 경우 알람등에 사용 */
	private ExceptionHandler exceptionHandler;
	/** 큐 한도. 메모리 아웃이 걱정된다면 조절해야한다. */
	private int queueCapacity = 50000;
	
	public synchronized void startup(){
		Preconditions.checkState(queue != null);
		Preconditions.checkState(csvLogInfoMap.size() > 0);
		thread = new CsvLogThread(this);
		thread.start();
	}
	
	/** 즉시 멈춘다. 큐에 대기중인 자료는 소실된다.  */
	public synchronized void interrupt() throws InterruptedException{
		thread.interrupt();
	}
	
	/** 
	 * 큐에 들어간 자료가 다 소진되기를 기다렸다가 스래드를 종료한다.
	 * 큐에 자료가 들어가는 행위가 먼저 멈춰야 한다. 
	 *  */
	public synchronized void stopAndjoin() throws InterruptedException{
		thread.setStop(true);
		thread.join(1000*10); //스프링에서 컨테이너 빌드세 예외가 발생하면 destry하는데, 여기서 행이 걸리수도 있다. 때문에  10초간 기다린다. 
	}
	
	/**  CsvLogInfo에 queue를 연결해주는 기능을 한다.  */ 
	public synchronized void add(CsvLogInfo<?> info){
		if(queue==null){
			queue = new ArrayBlockingQueue<CsvLog>(queueCapacity);
		}
		CsvLogInfo<?> exist = csvLogInfoMap.put(info.getName(),info);
		Preconditions.checkState(exist==null,"중복된 CsvLogInfo가 존재합니다 " + info.getName());
		info.setQueue(queue);
		info.init();
	}
	
	public int size() {
		return queue.size();
	}
	
	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}


}
