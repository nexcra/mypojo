
package erwins.util.vender.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Quart용 JobBean Spring의 Autowire를 사용하기 위해 이것을 통한다. <br> 아쉽게도 List타입의 프로퍼티는
 * 지원하지 않는다. ex) 아래 예의 #부분에 class이름을 적으면 된다. <bean id="customJobBean"
 * class="org.springframework.scheduling.quartz.JobDetailBean"
 * p:jobClass="erwins.util.vender.quartz.JobBean" > <property
 * name="jobDataAsMap"><map><entry key="jobRunable"
 * value-ref="#customJobRunner#"/></map></property> </bean>
 * 
 * @author erwins(quantum.object@gmail.com)
 */
public class JobBean extends QuartzJobBean {

    protected static Log log = LogFactory.getLog(JobBean.class);

    protected Runnable runnable;

    /**
     * 명시적으로 setter를 지정해 주어야 한다.
     */
    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    /** overriding하세요 */
    public boolean isRunAble() {
        return true;
    }

    /**
     * RUN!!
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if (runnable == null) throw new JobExecutionException("no JobRunable! see config file");
        if (!isRunAble()) return;
        log.debug(" ===== QuartzJobBean Start! =====");
        runnable.run();
        log.debug(" ===== QuartzJobBean End! =====");
    }

}
