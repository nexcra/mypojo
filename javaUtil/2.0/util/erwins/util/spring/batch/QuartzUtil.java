package erwins.util.spring.batch;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.google.common.collect.Lists;

/** 
 * 간단 유틸. 갈데가 없어서 걍 여기 넣어놨다.
 *  */
public abstract class QuartzUtil{
    
    /** 크론표현식 트리거의 경우 트리거가 생성될때 이미 스케쥴링이 생성된다.
     *   -> 즉 미리 생성해놓고 나중에 적용하면 이미 지나간 거 날자분량까지 전부 실행된다. 주의할것 */
    protected <T extends Job> List<JobKey> reloadTrigger( Scheduler scheduler,JobDataMap jobDataMap,Class<T> clazz, List<BatchJobExecution> jobs) throws SchedulerException {
        scheduler.clear();
        List<JobKey> jobKeys = Lists.newArrayList();
        for(BatchJobExecution each : jobs){
            String expression = each.getCronExpression();
            if(expression==null) continue;
            JobKey key = new JobKey(each.getJobName());
            JobDetail job = newJob(clazz).withIdentity(key).usingJobData(jobDataMap).build();
            Trigger trigger = newTrigger().withSchedule(cronSchedule(expression)).build();
            scheduler.scheduleJob(job,trigger);
            jobKeys.add(key);
        }
        return jobKeys;
    }
    
    /** 다음 잡의 실행 시각을 리턴한다. */
    public static Date nextFireTime(Scheduler scheduler,JobKey jobKey) throws SchedulerException {
    	@SuppressWarnings("unchecked")
		List<Trigger> triggers =  (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
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
