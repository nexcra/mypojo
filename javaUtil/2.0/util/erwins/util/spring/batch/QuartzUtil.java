package erwins.util.spring.batch;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/** 
 * 간단 유틸. 갈데가 없어서 걍 여기 넣어놨다.
 *  */
public abstract class QuartzUtil{
    
    /** 
     * 크론표현식 트리거의 경우 트리거가 생성될때 이미 스케쥴링이 생성된다.
     *   -> 즉 미리 생성해놓고 나중에 적용하면 이미 지나간 거 날자분량까지 전부 실행된다. 주의할것
     * @param jobs  배치명 / 크론 트리거로 구성
     **/
	public static <T extends Job> Multimap<JobDetail,Trigger> makeTriggers(JobDataMap jobDataMap,Class<T> clazz, Multimap<JobKey,String> jobs){
    	Multimap<JobDetail,Trigger> triggers = ArrayListMultimap.create();
		for(Entry<JobKey,Collection<String>> each : jobs.asMap().entrySet()){
			JobKey key = each.getKey();
            JobDetail job = newJob(clazz).withIdentity(key).usingJobData(jobDataMap).build();
            for(String expression : each.getValue()){
            	Trigger trigger = newTrigger().withSchedule(cronSchedule(expression)).build();
            	triggers.put(job, trigger);
            }
        }
        return triggers;
    }
	
	public static void scheduleJob(Scheduler scheduler,Multimap<JobDetail,Trigger> triggers) throws SchedulerException{
        for(Entry<JobDetail, Collection<Trigger>> entry : triggers.asMap().entrySet()){
        	for(Trigger each : entry.getValue()){
        		scheduler.scheduleJob(entry.getKey(), each);	
        	}
        }
    }
	
	/** 단축 메소드 */
	public static <T extends Job> void makeTriggerAndScheduleJob(Scheduler scheduler,JobDataMap jobDataMap,Class<T> clazz, Multimap<JobKey,String> jobkeys) throws SchedulerException{
		Multimap<JobDetail,Trigger> triggers = QuartzUtil.makeTriggers(jobDataMap, clazz, jobkeys);
		scheduleJob(scheduler,triggers);
    }
    
    /** 
     * 다음 잡의 실행 시각을 리턴한다.
     * 다수의 트리거가 있을 경유 가장 가까운 트리거를 리턴한다.
     *  */
    public static Date nextFireTime(Scheduler scheduler,JobKey jobKey) throws SchedulerException {
		List<? extends Trigger> triggers =  scheduler.getTriggersOfJob(jobKey);
    	if(triggers==null || triggers.size()==0) return null;
    	
    	List<Date> fires = Lists.newArrayList();
    	for(Trigger trigger : triggers){
    		fires.add(trigger.getNextFireTime());
    	}
    	//다수의 트리거중 가장 가까운 트리거를 리턴한다.
    	Collections.sort(fires);
        return fires.get(0);
    }
    

}
