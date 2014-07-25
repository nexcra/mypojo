package erwins.util.spring.batch;

import lombok.Data;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.InitializingBean;

/**
 * 스래드 안전하게 간단 위임
 */
@Data
public class DelegateThreadsafeItemReader<T> implements InitializingBean,ItemReader<T>,ItemStream{
    
	private ItemReader<T> delegate;
	
	/** 일단 샘플1 */
	public static <T>  DelegateThreadsafeItemReader<T> createFlatFileItemReader(){
		DelegateThreadsafeItemReader<T> reader = new DelegateThreadsafeItemReader<T>();
		reader.delegate = new FlatFileItemReader<T>();
		return reader;
	}
	
	/** 일단 샘플2 */
	public static <T>  DelegateThreadsafeItemReader<T> createMultiResourceItemReader(){
		DelegateThreadsafeItemReader<T> reader = new DelegateThreadsafeItemReader<T>();
		reader.delegate = new MultiResourceItemReader<T>();
		return reader;
	}
	
	
	/** 이부분을 스래드 세이프하게 변경 */
	public synchronized T  read() throws Exception, UnexpectedInputException, ParseException,NonTransientResourceException {
		return delegate.read();
	}

	public void close() throws ItemStreamException {
		if(delegate instanceof ItemStream) ((ItemStream)delegate).close();
	}

	public void open(ExecutionContext arg0) throws ItemStreamException {
		if(delegate instanceof ItemStream) ((ItemStream)delegate).open(arg0);
	}

	public void update(ExecutionContext arg0) throws ItemStreamException {
		if(delegate instanceof ItemStream) ((ItemStream)delegate).update(arg0);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(delegate instanceof InitializingBean){
			((InitializingBean)delegate).afterPropertiesSet();
		}
	}
	
    
}
