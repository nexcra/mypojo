package erwins.util.vender.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import erwins.util.tools.StopWatch;

/**
 * Quart용 JobBean Spring의 Autowire를 사용하기 위해 이것을 통한다. 아쉽게도 List타입의 프로퍼티는 지원하지 않는다. ex) 아래 예의 #부분에 class이름을 적으면 된다. <bean id="customJobBean" class="org.springframework.scheduling.quartz.JobDetailBean" p:jobClass="erwins.util.vender.quartz.JobBean" > <property name="jobDataAsMap"><map><entry key="jobRunable" value-ref="#customJobRunner#"/></map></property>        </bean>
 * @author  erwins(quantum.object@gmail.com)
 */
public class JobBean extends QuartzJobBean {
    
    protected static Log log = LogFactory.getLog(JobBean.class);
    
    /**
     * @uml.property  name="jobRunable"
     * @uml.associationEnd  
     */
    protected JobRunable jobRunable;
    
    /**
     * @param jobRunable
     * @uml.property  name="jobRunable"
     */
    public void setJobRunable(JobRunable jobRunable) {
        this.jobRunable = jobRunable;
    }
    
    /** overriding하세요 */
    public boolean isRunAble() {
        return true;
    }

    /**
     * RUN!!
     */
    @Override
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {        
        if(jobRunable==null) throw new JobExecutionException("no JobRunable! see config file");
        if(!isRunAble()) return ;
        
        StopWatch.initThreadTime();
        log.info("QuartzJobBean Start!");
        jobRunable.jobRun();
        log.info("QuartzJobBean End!");
        log.info(StopWatch.getThreadTimeStr());
	}

}
