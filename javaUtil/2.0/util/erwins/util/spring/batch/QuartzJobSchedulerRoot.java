package erwins.util.spring.batch;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/** 
 * 스케줄러를 기동한다.
 * 추상객체보다 유틸로 만드는게 좋아보인다?
 *  */
public abstract class QuartzJobSchedulerRoot{
    
    protected Scheduler scheduler;
    /** 초기화 이후  불변해야 한다. */
    protected JobDataMap jobDataMap;
    protected List<JobKey> jobKeys = new ArrayList<JobKey>();
    
    /** 크론표현식 트리거의 경우 트리거가 생성될때 이미 스케쥴링이 생성된다.
     *   -> 즉 미리 생성해놓고 나중에 적용하면 이미 지나간 거 날자분량까지 전부 실행된다. 주의할것 */
    protected <T extends Job> void reloadTrigger( Class<T> clazz, List<BatchJob> jobs) throws SchedulerException {
        scheduler.clear();
        for(BatchJob each : jobs){
            String expression = each.getCronExpression();
            if(expression==null) continue;
            JobKey key = new JobKey(each.getJobName());
            JobDetail job = newJob(clazz).withIdentity(key).usingJobData(jobDataMap).build();
            Trigger trigger = newTrigger().withSchedule(cronSchedule(expression)).build();
            scheduler.scheduleJob(job,trigger);
            jobKeys.add(key);
        }
    }
    
    /** 다음 잡의 실행 시각을 리턴한다. */
    public Map<String,Date> nextFireTimes() throws SchedulerException {
        Map<String,Date> map = new HashMap<String,Date>();
        for(JobKey each : jobKeys){
            @SuppressWarnings("unchecked")
            List<Trigger> triggers =  (List<Trigger>) scheduler.getTriggersOfJob(each);
            //이부분 주의. 트리거가 여러개 리턴될 수 있다
            if(triggers!=null && triggers.size() > 0){
                Trigger theOneTrigger = triggers.get(0);
                map.put(each.getName(), theOneTrigger.getNextFireTime());
            }
        }
        return map;
    }
    
    @PreDestroy
    public void shutdown() throws SchedulerException{
        if(scheduler!=null) scheduler.shutdown();
    }
    

}
