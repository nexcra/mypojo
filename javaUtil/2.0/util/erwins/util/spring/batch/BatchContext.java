package erwins.util.spring.batch;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;


/**
 * 스프링 배치의 ExecutionContext를 래핑하는 도우미
 * 각 프로젝트별로 확장해서 사용
 * 스래드 세이프 하게 적용할것!!
 * JOB 스코프에서 사용되는 Context의 경우 DB저장을 원하지 않는다면 사용후 삭제해 주자 
 * 
 * 2.1.8 이후 잡 파라메터 부분에 수정이 있었다. 
 * @author sin
 */
public class BatchContext{
    
	public static final ExitStatus COMPLETED = new ExitStatus("COMPLETED");
	public static final FlowExecutionStatus CONTINUE = new FlowExecutionStatus("CONTINUE");
    
    /** 정상 완료된 상태인가?
     * @AfterStep 등에서 사용한다 */
    public boolean isCompletedStep (){
        return isCompleted(se.getExitStatus());
    }
    public static boolean isCompleted(ExitStatus es){
        return COMPLETED.equals(es);
    }

    public final JobInstance ji;
    public final JobParameters jp;
    public final JobExecution je;
    public final ExecutionContext jec;
    /** null일 수 있다 */
    public StepExecution se;
    /** null일 수 있다 */
    public ExecutionContext sec;
    
    /** 잡 리스너에서 초기화된다 */
    public BatchContext(JobExecution je){
        this.je = je;
        jec = je.getExecutionContext();
        ji = je.getJobInstance();
        jp = je.getJobParameters();
        //StepExecution에는 마지막 스탭을 추가한다.
    }
    
    public BatchContext(StepExecution se){
        this.se = se;
        sec =  se.getExecutionContext();
        je = se.getJobExecution();
        jec = je.getExecutionContext();
        ji = je.getJobInstance();
        jp = je.getJobParameters();
    }
    
    public BatchContext(ChunkContext cc){
        se = cc.getStepContext().getStepExecution();
        sec =  se.getExecutionContext();
        je = se.getJobExecution();
        jec = je.getExecutionContext();
        ji = je.getJobInstance();
        jp = je.getJobParameters();
    }

    //==================  카운트 =========================
    
    /** 쓰기 취소 카운트를 1 올린다. */
    public void addWriteSkip(){
        se.setWriteSkipCount(se.getWriteSkipCount()+1);
    }
    public synchronized void addWriteSkip(int skipCount){
        se.setWriteSkipCount(se.getWriteSkipCount()+skipCount);
    }
    public void addReadSkip(){
        se.setReadSkipCount(se.getReadSkipCount()+1);
    }
    public synchronized void addReadSkip(int skipCount){
        se.setReadSkipCount(se.getReadSkipCount()+skipCount);
    }
    public void addProcessSkip(){
        se.setProcessSkipCount(se.getProcessSkipCount()+1);
    }
    public synchronized void addProcessSkip(int skipCount){
        se.setProcessSkipCount(se.getProcessSkipCount()+skipCount);
    }
    
    //================== ExitStatus  =========================
    
    public void setAllExitStatus(ExitStatus exitStatus){
         setJobExitStatus(exitStatus);
         setStepExitStatus(exitStatus);
    }
    
    public void setJobExitStatus(ExitStatus exitStatus){
        je.setExitStatus(exitStatus);
    }
    
    public void setStepExitStatus(ExitStatus exitStatus){
        se.setExitStatus(exitStatus);
    }
    
    /** Step의 ExitCode접두어가 하나라도 일치한다면 Job종료코드를 변경한다. */
    public void changeJobExitCodeByStepPrefix(String prefix,ExitStatus status) {
        Collection<StepExecution> steps = je.getStepExecutions();
        for(StepExecution stepEx : steps){
            String code = stepEx.getExitStatus().getExitCode();
            if(code.startsWith(prefix)){
                je.setExitStatus(status);
                break;
            }
        }
    }
    
    //======================= 파라메터 입력 ==========================

    /** 동기화 해서 입력 (Job의ExecutionContext일 경우 ) */
    private synchronized Integer addSynchronized(ExecutionContext ex,String key,Integer value){
        return add(ex, key, value);
    }

    private Integer add(ExecutionContext ex, String key, Integer value) {
        Integer exist = ex.getInt(key, 0);
        Integer plused = exist+value;
        ex.putInt(key,plused );
        return plused;
    }
    
    /** 동기화 해서 입력 (Job의ExecutionContext일 경우 ) */
    public synchronized Long addSynchronized(ExecutionContext ex,String key,Long value){
        return add(ex, key, value);
    }

    private Long add(ExecutionContext ex, String key, Long value) {
        Long exist = ex.getLong(key, 0L);
        Long plused = exist+value;
        ex.putLong(key, plused);
        return plused;
    }
    
    /**  최대값만 남김. */
    private void putMax(ExecutionContext ex, String key, Long value) {
        Long exist = ex.getLong(key, 0L);
        if(exist > value) return ;
        ex.putLong(key, value);
    }
    /**  최대값만 남김. */
    private void putMax(ExecutionContext ex, String key, Integer value) {
    	Integer exist = ex.getInt(key, 0);
        if(exist > value) return ;
        ex.putInt(key, value);
    }
    
    /** JOB / STEP 둘다 추가한다 */
    public void add(String key,Integer value) {
        addSynchronized(jec,key,value);
        add(sec,key,value);
    }
    public void add(String key,Long value) {
        addSynchronized(jec,key,value);
        add(sec,key,value);
    }
    
    public Integer addJe(String key,Integer value) {
        return addSynchronized(jec,key,value);
    }
    public Long addJe(String key,Long value) {
        return addSynchronized(jec,key,value);
    }
    public Integer addSe(String key,Integer value) {
        return add(sec,key,value);
    }
    public Long addSe(String key,Long value) {
        return add(sec,key,value);
    }
    public void putMaxSe(String key,Integer value) {
    	synchronized (this) {
    		putMax(sec,key,value);	
		}
    }
    public void putMaxSe(String key,Long value) {
    	synchronized (this) {
    		putMax(sec,key,value);	
		}
    }
    
    //================== JOB간에 공유하는 임시데이터  =========================
    
    public static final String JOB_TEMP_DATA_KEY = "jobTempDataKey";
    
    /** 잡 리스너 AfterJob에서 삭제해주면 된다 */
    @SuppressWarnings("unchecked")
    public void removeJobTempData() {
		Map<String,Object> tempDate = (Map<String,Object>) jec.get(JOB_TEMP_DATA_KEY);
    	if(tempDate!=null) {
    		tempDate.clear();  //GC 최적화
    		jec.put(JOB_TEMP_DATA_KEY, null);
    	}
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getJobTempData(String key) {
		Map<String,Object> tempDate = (Map<String,Object>) jec.get(JOB_TEMP_DATA_KEY);
		if(tempDate==null) return null;
		T obj = (T) tempDate.get(key);
    	return obj;
    }
    
    @SuppressWarnings("unchecked")
    /** 임시 데이터 사용을 알리기 위한 래퍼 */
    public void putJobTempData(String key,Object value) {
		Map<String,Object> tempDate = (Map<String,Object>) jec.get(JOB_TEMP_DATA_KEY);
    	if(tempDate==null) {
    		tempDate = new ConcurrentHashMap<String,Object>();
    		jec.put(JOB_TEMP_DATA_KEY, tempDate);
    	}
    	tempDate.put(key, value);
    }

}
