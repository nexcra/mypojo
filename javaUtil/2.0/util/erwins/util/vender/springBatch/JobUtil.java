package erwins.util.vender.springBatch;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;


/**
 * 간단 배치 유틸
 * 나중에 getStepEx -> getStepEc로 변경하자
 * @author sin
 */
public abstract class JobUtil{
	
	public static final String COMPLETED = "COMPLETED";
    
    public static ExecutionContext getStepEx(ChunkContext arg1){
        return arg1.getStepContext().getStepExecution().getExecutionContext();
    }
    
    /** Job의 ExecutionContext를 리턴한다. */
    public static ExecutionContext getJobEx(StepExecution stepExecution){
        return stepExecution.getJobExecution().getExecutionContext();
    }
    /** Job의 ExecutionContext를 리턴한다. */
    public static ExecutionContext getJobEx(ChunkContext arg1){
        return arg1.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }
    /** Job의 JobParameters를 리턴한다. */
    public static JobParameters getJobParameters(ChunkContext arg1){
        return getJobInstance(arg1).getJobParameters();
    }
    /** Job의 JobInstance를 리턴한다. */
    public static JobInstance getJobInstance(ChunkContext arg1){
        return arg1.getStepContext().getStepExecution().getJobExecution().getJobInstance();
    }
    /** Job의 JobInstance를 리턴한다. */
    public static JobInstance getJobInstance(StepExecution stepExecution){
        return stepExecution.getJobExecution().getJobInstance();
    }
    
    /** 누계 */
    public static void add(ExecutionContext ex,String key,Integer value){
        Integer exist = ex.getInt(key, 0);
        ex.putInt(key, exist+value);
    }
    public static void add(ExecutionContext ex,String key,Long value){
        Long exist = ex.getLong(key, 0L);
        ex.putLong(key, exist+value);
    }
    /** 동기화 해서 입력 (Job의ExecutionContext일 경우 ) */
    public static void addSynchronized(ExecutionContext ex,String key,Integer value){
        synchronized (ex) {
            Integer exist = ex.getInt(key, 0);
            ex.putInt(key, exist+value);    
        }
    }
    /** 쓰기 취소 카운트를 1 올린다. */
    public static void addWriteSkip(StepExecution stepExecution){
        stepExecution.setWriteSkipCount(stepExecution.getWriteSkipCount()+1);
    }
    
    public static void setAllExitStatus(StepExecution stepExecution,ExitStatus exitStatus){
        stepExecution.setExitStatus(exitStatus);
        stepExecution.getJobExecution().setExitStatus(exitStatus);
    }
    
    public static void setStepExitStatus(StepExecution stepExecution,ExitStatus exitStatus){
        stepExecution.setExitStatus(exitStatus);
    }
    public static void setJobExitStatus(ChunkContext arg1,ExitStatus exitStatus){
        arg1.getStepContext().getStepExecution().getJobExecution().setExitStatus(exitStatus);
    }
    
    /** Step의 ExitCode접두어가 하나라도 일치한다면 Job종료코드를 변경한다. */
    public static void changeJobExitCodeByStepPrefix(JobExecution je,String prefix,ExitStatus status) {
        Collection<StepExecution> steps = je.getStepExecutions();
        for(StepExecution stepEx : steps){
            String code = stepEx.getExitStatus().getExitCode();
            if(code.startsWith(prefix)){
                je.setExitStatus(status);
                break;
            }
        }
    }
    
    /** 스프링 배치의 shortContext를 map으로 변경해준다. 이름별로 소팅한다.  */
    public static Map<String,Object> shortContextToMap(String shortContext){
        Map<String,Object> result = new TreeMap<String, Object>();
        if(shortContext==null) return result;
        
        JSONObject body = JSONObject.fromObject(shortContext);
        Object map = body.get("map");
        if( !(map instanceof JSONObject )) return result;
        
        JSON json = (JSON) ((JSONObject)map).  get("entry");
        if(json.isArray()){
            JSONArray array = (JSONArray)json;
            for(int i=0;i<array.size();i++) addToMap(result, array.getJSONObject(i));
        }else addToMap(result, (JSONObject) json);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private static void addToMap(Map<String,Object> result, JSONObject each) {
        Set<String> keys = each.keySet();
        if(keys.size()==1){ //{"string":["append","2012_04_10_1118"]}
            JSONArray value = each.getJSONArray("string");
            result.put(value.getString(0), value.get(1).toString());
        }else{ //,{"string":"keywordSize","int":0} 이런식의 구조
            String value = each.getString("string");
            if(keys.contains("int"))  result.put(value,  each.get("int"));
            if(keys.contains("long")) result.put(value,  each.get("long")); 
            if(keys.contains("double")) result.put(value,  each.get("double"));//요건 확인안됨
        }
    }
    
    /** 정상 완료된 상태인가?
     * @AfterStep 등에서 사용한다 */
    public static boolean isCompleted (StepExecution se){
        return COMPLETED.equals(se.getExitStatus().getExitCode());
    }
    

}
