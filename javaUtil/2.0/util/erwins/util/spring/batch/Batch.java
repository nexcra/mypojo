package erwins.util.spring.batch;

import java.util.Date;

/**
 * 스프링배치의 기본VO
 * BatchJob이 이것을 상속한다
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * @author sin
 */
public class Batch{

    private Long jobExecutionId;
    
    private Date createTime; //스텝에는 없고 잡에만 있다. 그냥 쓴다.
    private Date startTime;
    private Date endTime;
    private String status;
    private String exitCode;
    private String exitMessage;
    private Date lastUpdated;
    
    private Long intervalMs;
    private Long lastUpdatedMs;
    
    private String jobName;
    
    /** 계산 후 리턴 */
    public Long getLastUpdatedMs() {
    	if(lastUpdatedMs!=null) return lastUpdatedMs;
    	if(getEndTime()==null) lastUpdatedMs = 0L;
    	else lastUpdatedMs = getEndTime().getTime() - getStartTime().getTime();
        return lastUpdatedMs;
    }
    
    
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getExitCode() {
        return exitCode;
    }
    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }
    public String getExitMessage() {
        return exitMessage;
    }
    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }
    public Date getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public Long getJobExecutionId() {
        return jobExecutionId;
    }
    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public Long getIntervalMs() {
        return intervalMs;
    }
    public void setIntervalMs(Long intervalMs) {
        this.intervalMs = intervalMs;
    }
    public void setLastUpdatedMs(Long lastUpdatedMs) {
        this.lastUpdatedMs = lastUpdatedMs;
    }
    

}
