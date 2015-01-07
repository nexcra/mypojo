package erwins.util.spring.batch;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 스프링배치의 기본VO
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * @author sin
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class BatchJobExecution extends BatchExecution{

	//================== BATCH_JOB_INSTANCE (걍 1:1로 간주) ====================
	private Long jobInstanceId;
	private String jobName;
	private String jobKey;
	
	//================== BATCH_JOB_EXECUTION ====================
    private Long jobExecutionId;
    private Date createTime; //스텝에는 없고 잡에만 있다. 그냥 쓴다.
    
    /** 추가옵션 */
    private Long nextFireMs;
    
    private String cronExpression;
    
    
}
