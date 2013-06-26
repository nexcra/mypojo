package erwins.util.spring.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

/**
 * ItemWriter의 scope는 언제나 step이다. (ExecutionContext를 쓴다면)
 * @author sin
 */
public abstract class SimpleItemWriter<T> implements ItemWriter<T>{
    
    protected ExecutionContext ec;
    
    @BeforeStep
    public void beforeStepByAnnotation(StepExecution stepExecution) {
        if(ec!=null) throw new RuntimeException("ec is aleady exist. check bean's scope : @Scope('step')");
        ec  = JobUtil.getJobEx(stepExecution);
        beforeStep(stepExecution);
    }
    
    protected void beforeStep(StepExecution stepExecution){
        //아무것도 하지 않는다. 필요하다면 오버라이드 하자.
    }

}
