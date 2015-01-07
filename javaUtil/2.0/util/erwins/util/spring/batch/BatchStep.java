package erwins.util.spring.batch;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 스프링배치의 기본VO
 * 자세한것은 스프링배치 홈페이지의 문서를 참고
 * @author sin
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class BatchStep extends BatchExecution{

    //================== BATCH_STEP_EXECUTION ====================
    private Long stepExecutionId;
    private String stepName;
    private Long commitCount;
    private Long readCount;
    private Long filterCount;
    private Long writeCount;
    private Long readSkipCount;
    private Long writeSkipCount;
    private Long processSkipCount;
    private Long rollbackCount;

}
