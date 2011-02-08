
package erwins.util.vender.apache;


/**
 * 커스텀한 로그. 아파치 로거에 파라메터 / Trace기능을 더했다.
 */
public interface _Log{
	
	public void trace(String format,Object ... args);
	public void debug(String format,Object ... args);
	public void info(String format,Object ... args);
	public void warn(String format,Object ... args);
	public void error(String format,Object ... args);
	public boolean isDebugEnabled();
	public boolean isTraceEnabled();
	public boolean isInfoEnabled();
	public String getClassName();

}
