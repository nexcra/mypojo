package erwins.util.tools.csvLog;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLog;
import erwins.util.tools.csvLog.CsvLogMamager.CsvLogInfo;


/** 
 * 별도의 스래드로 동작해서 파일에 쓴다. 
 * 한개의 스래드에서만 동작함으로 스래드 안전하다.
 * */
public class CsvLogThread extends Thread{

	private final Map<String,CsvLogInfo<?>> csvLogInfoMap;
	private final BlockingQueue<CsvLog> queue;
	private final CsvLogMamager csvLogMamager;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private volatile boolean stop = false;
	
	public CsvLogThread(CsvLogMamager csvLogMamager){
		this.queue = csvLogMamager.queue;
		this.csvLogInfoMap = csvLogMamager.csvLogInfoMap;
		this.csvLogMamager = csvLogMamager;
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
					csvLogMamager.reloadWriter(info);
					log.debug("{} : 새로운 파일 생성 {}",info.getName(),info.getWriterFile().getAbsolutePath());
				}
				CSVWriter writer = info.getWriter();
				String[] data = csvLog.getData();
				if(data!=null) writer.writeNext(data);
				if(csvLog.isFlush()) writer.flush();
			}
		} catch (InterruptedException e) {
			log.info("CsvLogThread 스래드 InterruptedException. 즉시 스래드를 종료합니다.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			for (Entry<String, CsvLogInfo<?>> entry : csvLogInfoMap.entrySet()) {
				try {
					csvLogMamager.close(entry.getValue());
				} catch (IOException e) {
					log.error("스레드 종료중 예외. 이 예외는 무시됨",e);
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
