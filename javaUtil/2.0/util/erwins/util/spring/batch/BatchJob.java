package erwins.util.spring.batch;

import java.util.Collection;

/**
 * 스프링배치의 기본VO
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * @author sin
 */
public class BatchJob extends Batch{

    private Long jobInstanceId;
    
    private String jobNameKr;
    private String cronExpression;
    private String jobType;
    private boolean enabled;
    private String jobPeriod;
    private int sortOrder;
    private String workType;
    private String description;
    private String onErrorAlert;//추가
    
    /** 파라메터 검색용 */
    private String[] jobNames;
    private String[] jobTypes;
    private String[] jobPeriods;
    private String searchDateType;
    
    /** 추가옵션 */
    private Long nextFireMs;
    
    private int totalCount;
    private int todayCount;
    private Collection<String> stepNames;
    
    public Long getJobInstanceId() {
        return jobInstanceId;
    }
    public void setJobInstanceId(Long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }
    public String getJobNameKr() {
        return jobNameKr;
    }
    public void setJobNameKr(String jobNameKr) {
        this.jobNameKr = jobNameKr;
    }
    public String getJobType() {
        return jobType;
    }
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    public String[] getJobNames() {
        return jobNames;
    }
    public void setJobNames(String[] jobNames) {
        this.jobNames = jobNames;
    }
    public String getSearchDateType() {
        return searchDateType;
    }
    public void setSearchDateType(String searchDateType) {
        this.searchDateType = searchDateType;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    public int getTodayCount() {
        return todayCount;
    }
    public void setTodayCount(int todayCount) {
        this.todayCount = todayCount;
    }
    public Collection<String> getStepNames() {
        return stepNames;
    }
    public void setStepNames(Collection<String> stepNames) {
        this.stepNames = stepNames;
    }
    public String getJobPeriod() {
        return jobPeriod;
    }
    public void setJobPeriod(String jobPeriod) {
        this.jobPeriod = jobPeriod;
    }
    public int getSortOrder() {
        return sortOrder;
    }
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    public String[] getJobTypes() {
        return jobTypes;
    }
    public void setJobTypes(String[] jobTypes) {
        this.jobTypes = jobTypes;
    }
    public String[] getJobPeriods() {
        return jobPeriods;
    }
    public void setJobPeriods(String[] jobPeriods) {
        this.jobPeriods = jobPeriods;
    }
    public String getWorkType() {
        return workType;
    }
    public void setWorkType(String workType) {
        this.workType = workType;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOnErrorAlert() {
        return onErrorAlert;
    }
    public void setOnErrorAlert(String onErrorAlert) {
        this.onErrorAlert = onErrorAlert;
    }
    public Long getNextFireMs() {
        return nextFireMs;
    }
    public void setNextFireMs(Long nextFireMs) {
        this.nextFireMs = nextFireMs;
    }

}
