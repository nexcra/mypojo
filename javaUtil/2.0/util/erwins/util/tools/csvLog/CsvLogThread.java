package erwins.util.tools.csvLog;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;
import erwins.util.root.exception.ExceptionHandler;
import erwins.util.root.exception.PropagatedRuntimeException;


/** 
 * 별도의 스래드로 동작해서 파일에 쓴다. 
 * 한개의 스래드에서만 동작함으로 스래드 안전하다.
 * */
@ThreadSafe
class CsvLogThread extends Thread{

	private final Map<String,CsvLogInfo<?>> csvLogInfoMap;
	private final BlockingQueue<CsvLog> queue;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private volatile boolean stop = false;
	private final ExceptionHandler exceptionHandler;
	
	/** 로깅하던 파일 aa.log를 스래드가 중단되었을때 aa_2014-03-11.csv로 변경할것인가
	 * 어차피 강종되면 작동하지 않는다.  */
	private boolean renameOnInterrupted = true;
	
	public CsvLogThread(CsvLogMamager csvLogMamager){
		this.queue = csvLogMamager.getQueue();
		this.csvLogInfoMap = csvLogMamager.getCsvLogInfoMap();
		this.exceptionHandler = csvLogMamager.getExceptionHandler();
		setName(this.getClass().getSimpleName());
	}
	
	@Override
	public void run() {
		log.info("CsvLogThread 스래드를 기동합니다");
		try {
			while(true){
				if(stop && queue.size()==0){
					//강제 종료가 아닌, 큐를 다 처리하고 종료하는 로직. WAS를 강제종료하지 않고, 셧다운 하는 경우라면 훅이나 스프링의@preDestroy등에 걸어주자.
					break;
				}
				CsvLog csvLog = queue.take();
				long current = System.currentTimeMillis();
				CsvLogInfo<?> info = csvLogInfoMap.get(csvLog.getName());
				if(info.getNextInterval() <= current){
					info.reloadWriter();
					log.debug("{} : 새로운 파일 생성 {}",info.getName(),info.getWriterFile().getAbsolutePath());
				}
				CSVWriter writer = info.getWriter();
				String[] data = csvLog.getData();
				//null이 입력되는 경우는 롤링파일을 교체하기위한 이벤트이다.
				if(data!=null) writer.writeNext(data);
				if(csvLog.isFlush()) writer.flush();
			}
		} catch (InterruptedException e) {
			log.info("CsvLogThread 스래드 InterruptedException. 즉시 스래드를 종료합니다.");
		} catch (Throwable e) {
			//핸들러가 있다면 위임한다.
			if(exceptionHandler==null) {
				throw new  PropagatedRuntimeException(e);
			} else {
				exceptionHandler.handleException(e);
			}
		}finally{
			if(renameOnInterrupted){
				//아마 WAS가 강제종료된다면  InterruptedException을 받지 못함으로 이 로직은 작동하지 못할것이다. 혹시나 정상종료로 인터럽트 된다면 플러시 후  해당 로그의 직전 파일로 리네임한다. 
				for (Entry<String, CsvLogInfo<?>> entry : csvLogInfoMap.entrySet()) {
					try {
						CsvLogInfo<?> info = entry.getValue();
						DateTime from = new DateTime().property(info.getDateTimeFieldType()).roundFloorCopy();
						info.close(from);
					} catch (IOException e) {
						log.error("스레드 종료중 IO예외. 이 예외는 무시됨",e);
					}
				}
			}else{
				for (Entry<String, CsvLogInfo<?>> entry : csvLogInfoMap.entrySet()) {
					CsvLogInfo<?> info = entry.getValue();
					try {
						info.getWriter().close();
					} catch (IOException e) {
						log.error("스레드 종료중 IO예외. 이 예외는 무시됨",e);
					}
				}
			}
			
			log.info("CsvLogThread 스래드를 종료합니다. 남은 자료수 : "+queue.size());	
		}
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	

}
