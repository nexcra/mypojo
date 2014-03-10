package erwins.util.spring.batch.component;

import java.io.IOException;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.retry.RetryCallback;
import org.springframework.batch.retry.RetryContext;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;

import erwins.util.spring.batch.BatchContext;
import erwins.util.spring.batch.tool.SpringRetryConfig;
import erwins.util.spring.batch.tool.SpringRetryConfig.RetryResult;

/** 간단하게 대량입력할때 사용
 * @Scope("step") 인지 확인할것! */
public abstract class RetryItemWriter<T> implements ItemWriter<T>{
	
	protected SpringRetryConfig config = new SpringRetryConfig().setBackoffSec(5).setMaxAttempts(3)
			.addRetryableExceptions(DuplicateKeyException.class).addRetryableExceptions(DeadlockLoserDataAccessException.class);
	protected BatchContext context;
	
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) throws IOException {
    	context  = new BatchContext(stepExecution);
    	doBeforeStep();
    }
    
    /** 필요하면 오버라이드 */ 
    protected void doBeforeStep() throws IOException {
    	
    }
	
    protected abstract void doWrite(final List<? extends T> items,RetryContext retryContext);
	
	@Override
	public void write(final List<? extends T> items) throws Exception {
		RetryResult<Integer> result = config.doWithRetry(new RetryCallback<Integer>() {
		    public Integer doWithRetry(RetryContext retryContext) {
		    	doWrite(items, retryContext);
				return 0;
		    }
		});
		int retryCount = result.getContext().getRetryCount();
		context.add("retryCountSum",retryCount );
		context.putMaxSe("retryCountMax", retryCount);
	}
	


    
    

}
