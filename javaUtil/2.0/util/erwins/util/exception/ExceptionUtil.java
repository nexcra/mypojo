package erwins.util.exception;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.exception.ExceptionUtils;

import erwins.util.lib.StringUtil;

/** 예외 모음 ValidationException을 던진다.
 * 스프링 시큐리티의 ThrowableAnalyzer를 활용할것 */
public class ExceptionUtil extends ExceptionUtils{
	
	//IllegalStateException / 
	
	//private ThrowableAnalyzer throwableAnalyzer = new ThrowableAnalyzer();
	
	private static final String NULL_MESSAGE = "args must be not null";
    
    public static void throwIfNotExist(File ... files){
    	for(File each : files){
    		if(each==null) throw new BusinessException(NULL_MESSAGE);
        	if(!each.exists()) throw new BusinessException(StringUtil.format("file [{0}] is not found", each.getAbsolutePath()));	
    	}
    }
    
    public static void throwIfNull(Object ... args){
    	for(Object each : args) if(each==null) throw new BusinessException(NULL_MESSAGE);
    }
    
    public static RuntimeException unsupportedOperation(){
    	return new UnsupportedOperationException();
    }

	/** checked 예외를 런타임 예외로 바꿔준다. */ 
	public static void castToRuntimeException(Throwable e){
	    if(e instanceof ExecutionException) throw new RuntimeException(e.getCause());
	    else if(e instanceof RuntimeException) throw (RuntimeException)e;
	    throw new RuntimeException(e);
	}
    
    
}
