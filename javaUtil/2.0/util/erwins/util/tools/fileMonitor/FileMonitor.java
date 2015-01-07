package erwins.util.tools.fileMonitor;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import erwins.util.collections.FileList;
import erwins.util.root.exception.ExceptionHandler;



/** 
 * 주기적으로파일을 체크해서 읽어온다. 주로 CSV로그를 로컬파일에 쓰고, 그것을 읽어서 DB처리하는데 사용된다.
 * 또는 다수의 WAS에서 불규칙적으로 입력되는 파일을 하나의 WAS에서 싱글스래드 처리해야할 경우에도 유용한다. (메모리를 공유하지 않음으로 Queue로는 불가능하다.)
 * 외부 리소스를 사용하지 않아 빠르게 작동함으로 주기가 짧아도 된다.
 * 싱글스래드로만 동작하게 하자.  (패턴 안겹치게 주의)
 * 데이터를 처리하고 파일을 처리(삭제,리네임 등등)하기 전에 WAS가 강제종료된다면 트랜잭션이 없음으로 문제가 발행한다. 이렇게 안되길 기대하자 
 * (라이터의 커밋주기가 늘어나야 안정성이 올라갈것이다.)
 * 
 * 실 적용해 보니 데이터의 적체에 따라 처리안된 파일이 좀 쌓일 수 있더라.
 * 
 * 어차피 동기적으로 작동해야 함으로 스케쥴 스래드풀을 사용하지 않는다.
 * */
@Data
@EqualsAndHashCode(callSuper=false)
public class FileMonitor extends Thread{

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final List<FileMonitorInfo> checkList = Lists.newArrayList();
	/** 혹시나 처리되지 못한 예외 처리용 */
	private ExceptionHandler exceptionHandler;
	private int intervalSec =  5;
	
	/** 모니터링할 패턴을 입력한다. */
	public synchronized void addMonitor(FileMonitorInfo csvLogMonitorInfo){
		checkList.add(csvLogMonitorInfo);
	}
	
	public FileMonitor(){
		setName(this.getClass().getSimpleName());
	}
	
	@Override
	public void run() {
		log.info("CsvLogMonitor 스래드를 기동합니다");
		Thread current = Thread.currentThread(); // = this
		
		try {
			while(!current.isInterrupted()){
				for(FileMonitorInfo check : checkList){
					FileList list = new FileList(check.getDirectory(),check.getFileFilter());
					for(File file : list){
						try {
							check.getFileMonitorCallback().doFileCallback(file);
						} catch (RuntimeException e) {
							if(exceptionHandler==null) throw e;
							else exceptionHandler.handleException(e);
						}
					}
				}
				Thread.sleep(TimeUnit.SECONDS.toMillis(intervalSec));
			}
		} catch (InterruptedException e) {
			log.info("CsvLogMonitor 스래드 InterruptedException. 즉시 스래드를 종료합니다.");
		}
		log.info("CsvLogMonitor 스래드를 종료합니다");
	}

}
