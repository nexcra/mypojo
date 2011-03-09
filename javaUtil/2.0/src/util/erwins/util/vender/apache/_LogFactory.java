
package erwins.util.vender.apache;

import erwins.util.lib.StringUtil;
import erwins.util.vender.apache._LogTracer.LogTrace;


/** 
 * 
 * LogTracer 정리 및 기본로거 위임으로 대체.

    역 페이징 처리기 만들어서 ListInversed 대치.
 * */
public abstract class _LogFactory{
	
	public static _Log instance(Class<?> clazz){
		return new LogForParameter(clazz);
	}
	public static _Log instance(Class<?> clazz,_LogTracer tracer){
		return new LogForTrace(clazz,tracer);
	}
	public static _Log traceLog(Class<?> clazz,_LogTracer tracer){
		return new TraceLog(clazz,tracer);
	}
	
	/** 아파치 로그에 템플릿 파라메터 + 메모리 로깅기능을 추가했다. */
	private static class LogForTrace implements _Log{
		private final org.apache.commons.logging.Log log;
		private final String className;
		private final _LogTracer tracer;
		
		public LogForTrace(Class<?> clazz,_LogTracer tracer){
			log = org.apache.commons.logging.LogFactory.getLog(this.getClass());
			this.tracer = tracer;
			className = clazz.getName();
			tracer.registLoger(this);
		}
		
		public void trace(String format,Object ... args){
			if(!log.isTraceEnabled()) return;
			String message = StringUtil.format(format, args); 
			log.trace(message);
			tracer.addLog(className,LogTrace.TRACE,message);
		}
		public void debug(String format,Object ... args){
			if(!log.isDebugEnabled()) return;
			String message = StringUtil.format(format, args);
			log.debug(message);
			tracer.addLog(className,LogTrace.DEBUG,message);
		}
		public void info(String format,Object ... args){
			if(!log.isInfoEnabled()) return;
			String message = StringUtil.format(format, args);
			log.info(message);
			tracer.addLog(className,LogTrace.INFO,message);
		}
		public void warn(String format,Object ... args){
			String message = StringUtil.format(format, args);
			log.warn(message);
			tracer.addLog(className,LogTrace.WARN,message);
		}
		public void error(String format,Object ... args){
			String message = StringUtil.format(format, args);
			log.error(message);
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
		public String getClassName() {
			return className;
		}
	}	
	
	/** log4j를 사용하지 않을때 사용한다. */
	private static class TraceLog implements _Log{
		private final String className;
		private final _LogTracer tracer;
		
		public TraceLog(Class<?> clazz,_LogTracer tracer){
			this.tracer = tracer;
			className = clazz.getName();
			tracer.registLoger(this);
		}
		
		public void trace(String format,Object ... args){
			String message = StringUtil.format(format, args);
			tracer.addLog(className,LogTrace.TRACE,message);
		}
		public void debug(String format,Object ... args){
			String message = StringUtil.format(format, args);
			tracer.addLog(className,LogTrace.DEBUG,message);
		}
		public void info(String format,Object ... args){
			String message = StringUtil.format(format, args);
			tracer.addLog(className,LogTrace.INFO,message);
		}
		public void warn(String format,Object ... args){
			String message = StringUtil.format(format, args);
			tracer.addLog(className,LogTrace.WARN,message);
		}
		public void error(String format,Object ... args){
			String message = StringUtil.format(format, args);
			tracer.addLog(className,LogTrace.ERROR,message);
		}
		public boolean isDebugEnabled(){
			return true;
		}
		public boolean isTraceEnabled(){
			return true;
		}
		public boolean isInfoEnabled(){
			return true;
		}
		public String getClassName() {
			return className;
		}
	}	
	
	/** 아파치 로그에 템플릿 파라메터를 추가했다. */
	private static class LogForParameter implements _Log{
		private final org.apache.commons.logging.Log log;
		private final String className;
		public LogForParameter(Class<?> clazz){
			log = org.apache.commons.logging.LogFactory.getLog(this.getClass());
			this.className = clazz.getName();
		}
		
		public void trace(String format,Object ... args){
			if(!log.isTraceEnabled()) return;
			log.trace(StringUtil.format(format, args));
		}
		public void debug(String format,Object ... args){
			if(!log.isDebugEnabled()) return;
			log.debug(StringUtil.format(format, args));
		}
		public void info(String format,Object ... args){
			if(!log.isInfoEnabled()) return;
			log.info(StringUtil.format(format, args));
		}	
		public void warn(String format,Object ... args){
			log.warn(StringUtil.format(format, args));
		}
		public void error(String format,Object ... args){
			log.error(StringUtil.format(format, args));
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
		public String getClassName() {
			return className;
		}
	}

}
