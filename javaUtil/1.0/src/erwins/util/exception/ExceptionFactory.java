package erwins.util.exception;

import java.io.File;

/** 예외 모음 */
public class ExceptionFactory{
	
    public static RuntimeException fileNotFound(File file){
    	return new MalformedException("file [{0}] is not found", file.getAbsolutePath());
    }
    public static void throwExeptionIfNotExist(File file){
    	if(!file.exists()) fileNotFound(file);
    }
    
    public static RuntimeException nullArgs(){
    	return new MalformedException("args must be not null");
    }
    
    
}
