package erwins.util.spring.batch;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/** 기존꺼 건드리기 싫어서 2로 만듬
 * DB에서 읽은 정보 전체를 메모리에 놓고 하나씩 읽어쓸때 사용된다.
 * 멀티스래드를 위해 synchronized를 사용한다.
 * @see IteratorItemReader2 
 *  */
@Deprecated
public abstract class SimpleItemReader<T> implements ItemReader<T>{
    
    private Iterator<T> it;
    protected ExecutionContext ec;
    @Resource private UniqueNameRunListener uniqueNameRunListener;

    /** synchronized 추가함 (Iterator는 스레드 안전하지 않다) */
    @Override
    public synchronized T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(!it.hasNext()) return null;
        return it.next();
    }
    
    /** 전체 데이터의 크기를 알 수 있을때 사용한다. */
    public void initReader(StepExecution stepExecution,Collection<T> items){
        if(ec!=null) throw new IllegalStateException("ec is aleady exist. check bean's scope : @Scope('step')");
        ec  = JobUtil.getJobEx(stepExecution);
        uniqueNameRunListener.setCurrentStepInfo(stepExecution, items.size());
        it = items.iterator();
    }
    
    /** 전체 데이터의 크기를 알 수 없을때 사용한다. 예상치가 있아면 입력한다. */
    public void initReader(StepExecution stepExecution,Iterator<T> it,Integer expect){
        if(ec!=null) throw new IllegalStateException("ec is aleady exist. check bean's scope : @Scope('step')");
        ec  = JobUtil.getJobEx(stepExecution);
        if(expect!=null) uniqueNameRunListener.setCurrentStepInfo(stepExecution, expect);
        this.it = it;
    }

}
