package erwins.util.spring.batch;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;

import erwins.util.root.Incompleted;


/** 
 * UniqueRunListener와는 달리 이름으로 구분한다.
 * 즉 하나의 리스너로 모든 진행중인 배치를 통제한다.
 * 
 * 개선의 여지가 있다.
 *  */
@Incompleted
public abstract class UniqueNameRunListener implements JobExecutionListener,Iterable<Entry<String,Long>>{
    
    /** 배치 이름과 현재 진행중인 스텝ID를 기록한다.
     * 이 이외의 정보는 DB를 조회해서 얻을 수 있다 */
    private Map<String,Long> currentRunnungJob = new ConcurrentHashMap<String,Long>();
    
    /** 해당 잡이 실행중인지 판단한다.
     * 특정 잡이 진행중일때를 기다리거나 하는 용도로 사용하자. */
    public boolean isExist(String ... jobNames){
        for(String jobName : jobNames) if(currentRunnungJob.containsKey(jobName)) return true;
        return false;
    }
    
    public void setCurrentStepInfo(StepExecution stepExecution,int size){
    	JobInstance ji = stepExecution.getJobExecution().getJobInstance();
        String jobName = ji.getJobName();
        currentRunnungJob.put(jobName, stepExecution.getId());
    }

    @Override
    public void beforeJob(JobExecution je) {
        String jobName = je.getJobInstance().getJobName();
        synchronized (this) {
            Long ecId = currentRunnungJob.get(jobName);
            if(ecId!=null) throw new BatchExistException("이미 배치가 기동중입니다. : " + jobName + " : " + ecId);
            currentRunnungJob.put(jobName, 0L);
        }
    }
    
    /** 예외를 명시적으로 찍어주어야 root console이 아닌 곳에도 남는다.
     * 동일 잡이 돌고있어서 발생한 예외라면 등록을 제거하지 않는다. */
    /**
     *  
     */
    @Override
    public void afterJob(JobExecution je) {
        String jobName = je.getJobInstance().getJobName();
        boolean isException = je.getStatus() != BatchStatus.COMPLETED;
        if(isException && isUniqueNameSkip(je)) return;
        //이하 로직 분리하니 주의!
        try{
        	afterProcess(jobName,isException,je);
            //if(isException) sendErrorSms(je, jobName);
            //JobUtil.changeJobExitCodeByStepPrefix(je, "SKIP",ExitCode.COMPLITE_WITH_SKIP);
        }finally{
            removeJob(jobName);
        }
    }

    private void removeJob(String jobName) {
        synchronized (this) {
            if(currentRunnungJob.containsKey(jobName)) currentRunnungJob.remove(jobName);
        }
    }

    /** 동일 배치가 돌고있어서 취소된 배치인지 */
    protected abstract boolean isUniqueNameSkip(JobExecution je);
    protected abstract boolean afterProcess(String jobName,boolean isException,JobExecution je);
    /*{
        List<Throwable> list = je.getAllFailureExceptions();
        if(BatchExistException.isBatchExistException(list)){
            je.setExitStatus(ExitCode.SKIP_R);
            return true;
        }
        return false;
    }*/

    @Override
    public Iterator<Entry<String, Long>> iterator() {
        return currentRunnungJob.entrySet().iterator();
    }
}
