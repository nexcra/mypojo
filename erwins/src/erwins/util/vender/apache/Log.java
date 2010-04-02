
package erwins.util.vender.apache;

import org.apache.commons.logging.LogFactory;

import erwins.util.lib.Strings;

/**
 * 파라메터라이즈 되는 로거.
 */
public class Log{
	private final org.apache.commons.logging.Log log;
	
	public Log(Class<?> clazz){
		log = LogFactory.getLog(this.getClass());
	}
	
	public void trace(String format,Object ... args){
		if(!log.isTraceEnabled()) return;
		log.trace(Strings.format(format, args));
	}
	public void debug(String format,Object ... args){
		if(!log.isDebugEnabled()) return;
		log.debug(Strings.format(format, args));
	}
	public void info(String format,Object ... args){
		if(!log.isInfoEnabled()) return;
		log.info(Strings.format(format, args));
	}	
	public void warn(String format,Object ... args){
		log.warn(Strings.format(format, args));
	}
	public void error(String format,Object ... args){
		log.error(Strings.format(format, args));
	}
	public boolean isDebugEnabled(){
		return log.isDebugEnabled();
	}
	public boolean isTraceEnabled(){
		return log.isTraceEnabled();
	}
	public boolean isInfoEnabled(){
		return log.isInfoEnabled();
	}


}
