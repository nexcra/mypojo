package erwins.util.lib;



public abstract class ExceptionUtil{
    
	
	/** 런타임 예외라면 그냥 던지고, 아니라면 래핑해서 던진다 */
	public static void throwException(Throwable e) throws RuntimeException{
		if(e instanceof RuntimeException){
			RuntimeException r = (RuntimeException) e;
			throw r;
		}
		throw new RuntimeException(e);
	}

}
