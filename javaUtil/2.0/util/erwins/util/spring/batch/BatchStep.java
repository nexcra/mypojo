package erwins.util.spring.batch;



/**
 * 스프링배치의 기본VO
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * @author sin
 */
public class BatchStep extends Batch implements Comparable<BatchStep>{

    private String stepName;
    private Long stepExecutionId;
    
    private Long commitCount;
    private Long readCount;
    private Long filterCount;
    private Long writeCount;
    private Long readSkipCount;
    private Long writeSkipCount;
    private Long processSkipCount;
    private Long rollbackCount;
    
    //추가.. 이 밑에꺼는 임시임. 정보만 제공하고 문자열 조합은 클라이언트에서 하자
    private Integer totalRead;
    /*
    private String avgTimeStr;
    private String remainTimeStr;
    private String percentStr;
    */
    
    public String getStepName() {
        return stepName;
    }
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    public Long getStepExecutionId() {
        return stepExecutionId;
    }
    public void setStepExecutionId(Long stepExecutionId) {
        this.stepExecutionId = stepExecutionId;
    }
    public Long getCommitCount() {
        return commitCount;
    }
    public void setCommitCount(Long commitCount) {
        this.commitCount = commitCount;
    }
    public Long getReadCount() {
        return readCount;
    }
    public void setReadCount(Long readCount) {
        this.readCount = readCount;
    }
    public Long getFilterCount() {
        return filterCount;
    }
    public void setFilterCount(Long filterCount) {
        this.filterCount = filterCount;
    }
    public Long getWriteCount() {
        return writeCount;
    }
    public void setWriteCount(Long writeCount) {
        this.writeCount = writeCount;
    }
    public Long getReadSkipCount() {
        return readSkipCount;
    }
    public void setReadSkipCount(Long readSkipCount) {
        this.readSkipCount = readSkipCount;
    }
    public Long getWriteSkipCount() {
        return writeSkipCount;
    }
    public void setWriteSkipCount(Long writeSkipCount) {
        this.writeSkipCount = writeSkipCount;
    }
    public Long getProcessSkipCount() {
        return processSkipCount;
    }
    public void setProcessSkipCount(Long processSkipCount) {
        this.processSkipCount = processSkipCount;
    }
    public Long getRollbackCount() {
        return rollbackCount;
    }
    public void setRollbackCount(Long rollbackCount) {
        this.rollbackCount = rollbackCount;
    }
    public Integer getTotalRead() {
        return totalRead;
    }
    public void setTotalRead(Integer totalRead) {
        this.totalRead = totalRead;
    }
    @Override
    public int compareTo(BatchStep arg0) {
        return stepExecutionId.compareTo(arg0.stepExecutionId);
    }
    /*
    public String getAvgTimeStr() {
        return avgTimeStr;
    }
    public String getRemainTimeStr() {
        return remainTimeStr;
    }
    public String getPercentStr() {
        return percentStr;
    }
    */
    

}
