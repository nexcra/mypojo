package erwins.util.spring.batch.tool;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.google.common.base.Preconditions;

import erwins.util.spring.batch.BatchContext;

/** 
 * 컨텍스트에 키를 모아놓고 드라이빙 처리한다.
 * 해당 스탭이 성공하지 못했더라도 다음으로 진행한다. 반드시 성공 여부에 따라 후처리 할것.
 *   */
public class ItemDecider implements JobExecutionDecider {
	
	private static final String LIST_KEY = "itemDeciderListKey";
	private static final String ITEM_KEY = "itemDeciderItemKey";
	
	public static void setList(BatchContext context,List<?> list){
		context.putJobTempData(LIST_KEY,list);
	}
	
	public static final FlowExecutionStatus CONTINUE = new FlowExecutionStatus("CONTINUE");
    
    /** 스텝 성공 기준 -> REMAIN_SIZE가 양수일때만 CONTINUE로 변경해준다  */
    @Override
    public FlowExecutionStatus decide(JobExecution arg0, StepExecution stepExecution) {
        BatchContext context = new BatchContext(stepExecution);
        
        List<?> ready =  context.getJobTempData(LIST_KEY);
        Preconditions.checkNotNull(ready, "ItemDecider 사용 이전에 LIST를 입력해야 합니다.");
        
        if(ready.size()==0) return FlowExecutionStatus.COMPLETED;
        
        Object currentStep =  ready.remove(0);
        context.putJobTempData(ITEM_KEY, currentStep);
        if(currentStep instanceof FlowExecutionStatusAble){
        	FlowExecutionStatusAble step = (FlowExecutionStatusAble) currentStep;
        	return step.getFlowExecutionStatus();
        }
        return CONTINUE;
    }
    
    public static interface FlowExecutionStatusAble{
    	public FlowExecutionStatus getFlowExecutionStatus();
    }
    

    

}
