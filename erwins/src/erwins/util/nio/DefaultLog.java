package erwins.util.nio;

import erwins.util.lib.Strings;

/** 걍 테스트용 로거이다. 추후 LOg4j로 변경. 특수한 용도에서 제한적으로만 사용할것. */
public class DefaultLog{
	
	public static final int DEBUG = 3 ;
	public static final int INFO = 5 ;
	
	/** 걍 귀찮아서.. ㄷㄷ */
	public static volatile int LEVEL = DEBUG;

	public void info(String msg,Object ... args) {
		if(LEVEL <= INFO){
			String message = Strings.format(msg, args);
			System.out.println(message);
		}
	}
	
	public void debug(String msg,Object ... args) {
		if(LEVEL <= DEBUG){
			String message = Strings.format(msg, args);
			System.out.println(message);	
		}
	}
	
}