package erwins.util.tools.csvLog;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.FileSystemResource;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import erwins.util.collections.FileList;
import erwins.util.collections.FileList.AntFileNameFilter;
import erwins.util.lib.FileUtil;
import erwins.util.spring.batch.tool.SpringBatchMock;



/** 
 * 주기적으로파일을 체크해서 읽어온다. 주로 CSV로그를 로컬파일에 쓰고, 그것을 읽어서 DB처리하는데 사용된다.
 * 또는 다수의 WAS에서 불규칙적으로 입력되는 파일을 하나의 WAS에서 싱글스래드 처리해야할 경우에도 유용한다. (메모리를 공유하지 않음으로 Queue로는 불가능하다.)
 * 외부 리소스를 사용하지 않아 빠르게 작동함으로 주기가 짧아도 된다.
 * 싱글스래드로만 동작하게 하자.  (패턴 안겹치게 주의)
 * 데이터를 처리하고 파일을 처리(삭제,리네임 등등)하기 전에 WAS가 강제종료된다면 트랜잭션이 없음으로 문제가 발행한다. 이렇게 안되길 기대하자 
 * (라이터의 커밋주기가 늘어나야 안정성이 올라갈것이다.)
 * 
 * 실 적용해 보니 데이터의 적체에 따라 처리안된 파일이 좀 쌓일 수 있더라.
 * CsvLogMonitorInfo는 별개의 클래스로 관리하는게 좋아보인다.
 * 
 * 나중에 추가 요구사항이 온다면 스케쥴 스래드풀로 수정
 * */
public class FileMonitor extends Thread{

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private List<CsvLogMonitorInfo> checkList = Lists.newArrayList();
	
	private int intervalSec =  5;
	
	/** 모니터링할 패턴을 입력한다.
	 * ex) new CsvLogMonitorInfo(new File("C:/DATA/download/로그파일"))
			.setRenameDir(new File("C:/DATA/download/로그파일2")).setAntPath("*.log").setSpringBatch(itemReader1, itemWriter, 2) */
	public synchronized void addMonitor(CsvLogMonitorInfo csvLogMonitorInfo){
		if(!csvLogMonitorInfo.directory.isDirectory()) csvLogMonitorInfo.directory.mkdirs();
		Preconditions.checkState(csvLogMonitorInfo.directory.isDirectory(),"디렉토리가 올바르지 않습니다." + csvLogMonitorInfo.directory.getAbsolutePath());
		
		//직접 구현했다면 this를 구현체로 등록 
		if(csvLogMonitorInfo.csvLogFileCallback==null && csvLogMonitorInfo instanceof CsvLogFileCallback){
			csvLogMonitorInfo.csvLogFileCallback = (CsvLogFileCallback)csvLogMonitorInfo;
		}
		if(csvLogMonitorInfo.csvLogFileFinishCallback==null && csvLogMonitorInfo instanceof CsvLogFileFinishCallback){
			csvLogMonitorInfo.csvLogFileFinishCallback = (CsvLogFileFinishCallback)csvLogMonitorInfo;
		}
		
		checkList.add(csvLogMonitorInfo);
	}
	
	public FileMonitor(){
		setName(this.getClass().getSimpleName());
	}
	
	@Override
	public void run() {
		log.info("CsvLogMonitor 스래드를 기동합니다");
		Thread current = Thread.currentThread(); // = this
		while(!current.isInterrupted()){
			for(CsvLogMonitorInfo check : checkList){
				FileList list = new FileList(check.getDirectory(),check.getFileFilter());
				for(File file : list){
					boolean success = false;
					try{
						check.csvLogFileCallback.doFileCallback(file);
						success = true;
					}finally{
						check.csvLogFileFinishCallback.doFileFinishCallback(file, success);
					}
				}
			}
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(intervalSec));
			} catch (InterruptedException e) {
				current.interrupt();
				log.info("CsvLogMonitor 스래드 InterruptedException. 즉시 스래드를 종료합니다.");
			}
		}
		log.info("CsvLogMonitor 스래드를 종료합니다");
	}
	
	/** 읽을 파일의 위치화 어떻게 처리할지의 설정파일 */
	@Data
	public static class CsvLogMonitorInfo{
		private final File directory;
		private FileFilter fileFilter;
		private CsvLogFileCallback csvLogFileCallback;
		private CsvLogFileFinishCallback csvLogFileFinishCallback;
		
		/** 
		 * 파일 이름으로만 체크한다. 풀패스 아님
		 * */
		public CsvLogMonitorInfo setAntPath(String antPath){
			fileFilter = new AntFileNameFilter(antPath).setDirectory(false);
			return this;
		}
		
		/** 파일 콜백을 리더/라이터로 대체한다. */
		public <T> CsvLogMonitorInfo setSpringBatch(final ResourceAwareItemReaderItemStream<T> itemReader,final ItemWriter<T> itemWriter,final int commitInterval){
			csvLogFileCallback = new CsvLogFileCallback() {
				@Override
				public void doFileCallback(File file) {
					itemReader.setResource(new FileSystemResource(file));
					SpringBatchMock.csvReadWrite(itemReader, itemWriter, commitInterval);
				}
			};
			return this;
		}
		
		/** 처리가 끝나면 디렉토리를 이동시킨다. */
		public CsvLogMonitorInfo setRenameDir(final File toDir){
			if(!toDir.isDirectory()) toDir.mkdirs();
			Preconditions.checkState(toDir.isDirectory());
			csvLogFileFinishCallback = new CsvLogFileFinishCallback() {
				@Override
				public void doFileFinishCallback(File file, boolean success) {
					if(!success) return;
					String name = file.getName();
					File newFile = new File(toDir,name);
					FileUtil.renameToUniqueName(file, newFile);
				}
			};
			return this;
		}
		public CsvLogMonitorInfo setDeleteFile(){
			csvLogFileFinishCallback = new CsvLogFileFinishCallback() {
				@Override
				public void doFileFinishCallback(File file, boolean success) {
					if(!success) return;
					FileUtil.delete(file);
				}
			};
			return this;
		}
		
	}
	
	/** 예외가 발생하지않게 잘 조절할것? */
	public static interface CsvLogFileCallback{
		public void doFileCallback(File file);
	}
	
	public static interface CsvLogFileFinishCallback{
		public void doFileFinishCallback(File file,boolean success);
	}

	public int getIntervalSec() {
		return intervalSec;
	}

	public void setIntervalSec(int intervalSec) {
		this.intervalSec = intervalSec;
	}

}
