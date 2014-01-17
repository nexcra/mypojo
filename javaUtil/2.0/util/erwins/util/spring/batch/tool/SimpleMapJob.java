package erwins.util.spring.batch.tool;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;

/** quartz를 사용하기위한 간단잡. 내가 못찾는건지 메소드인보크같은 고급기능은 없다. 
 *  스프링 빈을 쓰기 위해 임시방편으로 만들었다.  */
public class SimpleMapJob implements Job{
	
	private static final String MAP_KEY = SimpleMapJob.class.getName();
	
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
    	Tasklet task = (Tasklet) arg0.getMergedJobDataMap().get(MAP_KEY);
		try {
			task.execute(null, null);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
    }
	
    /** 간단 잡 추가용 */
	public static void scheduleJob(Scheduler scheduler,String jobName,Tasklet task,String cronSchedule) throws SchedulerException{
		JobKey key = new JobKey(jobName);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(MAP_KEY, task);
		JobDetail job = JobBuilder.newJob(SimpleMapJob.class).withIdentity(key).usingJobData(jobDataMap).build();
		Trigger trigger = TriggerBuilder.newTrigger().withSchedule(cronSchedule(cronSchedule)).build();
		scheduler.scheduleJob(job, trigger);
		
	}
    

}
