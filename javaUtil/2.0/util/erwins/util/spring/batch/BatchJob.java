package erwins.util.spring.batch;

import java.util.Date;
import java.util.List;

import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 스프링배치의 기본VO
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * 샘플임으로 확장하지 말고 복붙 해서 사용할것
 * @author sin
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(exclude={"batchSteps"})
public class BatchJob extends BatchExecution{

	//================== BATCH_JOB_INSTANCE (걍 1:1로 간주) ====================
	private Long jobInstanceId;
	private String jobName;
	private String jobKey;
	
	//================== BATCH_JOB_EXECUTION ====================
    private Long jobExecutionId;
    private Date createTime; //스텝에는 없고 잡에만 있다. 그냥 쓴다.
    private String jobConfigurationLocation;
    
    //================== XML ====================
    private String jobDescription;
    @OneToMany
    private List<BatchStep> batchSteps;
    
    ///** 추가옵션 */
    //private Long nextFireMs;
    /** 크론 표현식. 스케쥴링을 등록할때 사용된다. */
    private String cronExpression;
    
}
