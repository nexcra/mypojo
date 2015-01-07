package erwins.util.tools.fileMonitor;

import java.io.File;

import lombok.Data;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.FileSystemResource;

import erwins.util.root.exception.ExceptionHandler;
import erwins.util.spring.batch.tool.SpringBatchMock;
import erwins.util.spring.batch.tool.SpringBatchUtil;

/** 
 * 쓸만함.. 
 * */
@Data
public class FileMonitorBatchCallback<T> implements FileMonitorCallback{

	private ResourceAwareItemReaderItemStream<T> itemReader;
	/** 데이터 처리 후 파일이동등을 처리하라면 afterStep으로 처리 */
	private ItemWriter<T> itemWriter;
	private ExceptionHandler exceptionHandler;
	private int commitInterval;
	
	@Override
	public void doFileCallback(File file) {
		itemReader.setResource(new FileSystemResource(file));
		SpringBatchMock<T> mock = new SpringBatchMock<T>();
		mock.setCommitInterval(commitInterval);
		mock.setItemReader(itemReader);
		mock.setItemWriter(itemWriter);

		StepExecution sec = new StepExecution("sec",mock.getJobExecution()); //별다른 정보는 담지 않는다.
		try {
			mock.run();
			sec.setExitStatus(ExitStatus.COMPLETED);
		} catch (RuntimeException e) {
			sec.setExitStatus(ExitStatus.FAILED);
			if(exceptionHandler==null) throw e;
			else exceptionHandler.handleException(e);
		}finally{
			SpringBatchUtil.afterStepIfAble(itemWriter,sec);	
		}
	}
	

}
