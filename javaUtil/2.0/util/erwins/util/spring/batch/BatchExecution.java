package erwins.util.spring.batch;

import java.util.Date;

import lombok.Data;

/**
 * 스프링배치의 기본VO
 * BatchJob이 이것을 상속한다
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * @author sin
 */
@Data
public abstract class BatchExecution{

    private Date startTime;
    private Date endTime;
    
    private String status;
    private String exitCode;
    private String exitMessage;
    private Date lastUpdated;
    
    //====================== 추가됨 =========================
    private Long lastUpdatedMs;
    
    public void init() {
    	if(getEndTime()==null) lastUpdatedMs = 0L;
    	else lastUpdatedMs = getEndTime().getTime() - getStartTime().getTime();
    }
    

}
