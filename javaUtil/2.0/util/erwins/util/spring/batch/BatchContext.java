package erwins.util.spring.batch;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;



/**
 * 스프링 배치의 ExecutionContext를 래핑하는 도우미
 * 각 프로젝트별로 확장해서 사용
 * 스래드 세이프 하게 적용할것!!
 * JOB 스코프에서 사용되는 Context의 경우 DB저장을 원하지 않는다면 사용후 삭제해 주자 
 * @author sin
 */
public class BatchContext{
    
	@Deprecated
    public static final String COMPLETED = "COMPLETED";
    
    /** 정상 완료된 상태인가?
     * @AfterStep 등에서 사용한다 */
    public boolean isCompletedStep (){
        return COMPLETED.equals(se.getExitStatus().getExitCode());
    }
    public static boolean isCompleted(ExitStatus es){
        return COMPLETED.equals(es.getExitCode());
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
        jp = ji.getJobParameters();
        //StepExecution에는 마지막 스탭을 추가한다.
        
    }
    
    public BatchContext(StepExecution se){
        this.se = se;
        sec =  se.getExecutionContext();
        je = se.getJobExecution();
        jec = je.getExecutionContext();
        ji = je.getJobInstance();
        jp = ji.getJobParameters();
    }
    
    public BatchContext(ChunkContext cc){
        se = cc.getStepContext().getStepExecution();
        sec =  se.getExecutionContext();
        je = se.getJobExecution();
        jec = je.getExecutionContext();
        ji = je.getJobInstance();
        jp = ji.getJobParameters();
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

    //================== static  =========================
    
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
    
    
    private static final Gson GSON = new Gson();
    
    /** 스프링 배치의 shortContext를 map으로 변경해준다. (GSON 버전)  */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String,Object> shortContextToGMap(String shortContext){
    	Map<String,Object> result = Maps.newLinkedHashMap();
    	if(Strings.isNullOrEmpty(shortContext)) return result;
    	
		Map<String,Object> json =  GSON.fromJson(shortContext, Map.class);
		
		Object mapObject = json.get("map");
		if(mapObject==null) return result;
		if(mapObject instanceof StringMap){
			StringMap<String> map = (StringMap<String>) mapObject;
			addStringMap(result, map);
		}else if(mapObject instanceof List){
			List<StringMap> list = (List<StringMap>) mapObject;
			for(StringMap each : list) addStringMap(result, each); 
		}else throw new RuntimeException("알려지지 않은 타입입니다. " + mapObject);
		return result;
    }
    
	@SuppressWarnings("unchecked")
	private static void addStringMap(Map<String, Object> result, StringMap<String> map) {
		Object entry =  map.get("entry");
		if(entry instanceof List){
			List<Object> list = (List<Object>) entry;
			for(Object each : list) gsonObjectToValue(result,each);
		}else{
			gsonObjectToValue(result,entry);
		}
	}
    
    /** 스프링배치 컨텍스트를 GSON으로 읽은 값을, 일반 value로 변경해준다 **/
    @SuppressWarnings("unchecked")
    private static void gsonObjectToValue(Map<String,Object> result, Object each) {
    	Preconditions.checkState(each instanceof StringMap,each.getClass().getSimpleName());
    	
    	StringMap<Object> map = (StringMap<Object>) each;
    	int size = map.size();
    	Preconditions.checkState(size==1 || size == 2);
    	
    	if(size==1){
    		List<Object> array = (List<Object>)map.get("string");
    		Preconditions.checkState(array.size() == 2);
    		Object value = array.get(1);
    		//Gson은 타입을 지정안하면 1 -> 1.0 이렇게 변경해버린다. date형식이 숫자로 들어가는데 더블형태로 깨져서 임시방편 처리한다.
    		if(value instanceof Double) value = String.valueOf(((Double)value).longValue());  
    		result.put(array.get(0).toString(), value);
    	}else{
    		String key = (String) map.get("string");
    		Object value = null;
    		if(map.containsKey("int"))  {
    			value = map.get("int");
    			if(value instanceof Double)  value = ((Double)value).intValue();
    		}
    		else if(map.containsKey("long")){
    			value = map.get("long"); 
    			if(value instanceof Double)  value = ((Double)value).longValue();
    		}
    		else if(map.containsKey("double")){
    			value = map.get("double");//요건 확인안됨
    		}
            result.put(key,  value);
    	}
    }

}
