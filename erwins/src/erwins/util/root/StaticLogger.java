package erwins.util.root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * static에서 사용되는 logger이다.
 * 클래스 로더가 다수일때 로그가 꼬이는 등의 문제를 발생할 수 있으니 주의해서 사용해야 한다.
 * @author erwins(quantum.object@gmail.com)
 **/
public abstract class StaticLogger{
    
    protected static Log log = LogFactory.getLog(StaticLogger.class);
    
}
