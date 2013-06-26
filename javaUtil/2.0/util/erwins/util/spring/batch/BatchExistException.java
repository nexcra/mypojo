package erwins.util.spring.batch;

import java.util.List;


/**
 * 이미 진행중인 배치가 있을때 던진다. 
 * 이경우 배치의 초기화단계와 마무리단계를 무시한다
 * @author sin
 */
@SuppressWarnings("serial")
public class BatchExistException extends RuntimeException{

    public BatchExistException() {
        super();
    }

    public BatchExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchExistException(String message) {
        super(message);
    }

    public BatchExistException(Throwable cause) {
        super(cause);
    }
    
    public static boolean isBatchExistException(List<Throwable> list){
        for (Throwable each : list)  if(each instanceof BatchExistException) return true;
        return false;
    }

}
