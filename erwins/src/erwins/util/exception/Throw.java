package erwins.util.exception;

import java.util.concurrent.ExecutionException;



/**
 * 예외 래핑용 클래스
 */
public abstract class Throw{
    
    /** checked 예외를 런타임 예외로 바꿔준다. */ 
    public static void wrap(Throwable e){
        if(e instanceof ExecutionException) throw new RuntimeException(e.getCause());
        else if(e instanceof RuntimeException) throw (RuntimeException)e;
        throw new RuntimeException(e);
    }
    
    
}
