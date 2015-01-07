package erwins.util.spring.batch.component;

import lombok.Data;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.InitializingBean;

import erwins.util.spring.batch.tool.SpringBatchUtil;

/**
 * 스래드 안전하게 간단 위임
 */
@Data
public class ThreadsafeItemReader<T> implements InitializingBean,ItemReader<T>,ItemStream{
    
	private ItemReader<T> delegate;
	
	/** 일단 샘플1 */
	public static <T>  ThreadsafeItemReader<T> createFlatFileItemReader(){
		ThreadsafeItemReader<T> reader = new ThreadsafeItemReader<T>();
		reader.delegate = new FlatFileItemReader<T>();
		return reader;
	}
	
	/** 이부분을 스래드 세이프하게 변경 */
	public synchronized T  read() throws Exception, UnexpectedInputException, ParseException,NonTransientResourceException {
		return delegate.read();
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		SpringBatchUtil.openIfAble(delegate, executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		SpringBatchUtil.updateIfAble(delegate, executionContext);		
	}

	@Override
	public void close() throws ItemStreamException {
		SpringBatchUtil.closeIfAble(delegate);		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		SpringBatchUtil.afterPropertiesSetIfAble(delegate);
	}
	
    
}
