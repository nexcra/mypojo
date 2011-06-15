
package erwins.util.vender.apache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.util.lib.StringUtil;
import erwins.util.vender.apache.LogTracer.LogTrace;


/** 
 * 기본 로그팩토리에 편의메소드 추가
 * */
public abstract class LogFactory2{
	
	public static Log2 getLog(Class<?> clazz){
		return new DefaultLog(LogFactory.getLog(clazz));
	}
	
	public static interface Log2  extends Log{
		public abstract void trace(String format, Object... args);
		public abstract void debug(String format, Object... args);
		public abstract void info(String format, Object... args);
		public abstract void warn(String format, Object... args);
		public abstract void error(String format, Object... args);
	}
	
	public static interface LogCallback{
		public void log(String level,String message);
	}
	
	/** 진짜 어이없는 요구사항이 들어올때나 사용하자. */
	public static class LogForCallback implements Log2{
		private final org.apache.commons.logging.Log log;
		private final LogCallback logCallback;
		
		public LogForCallback(Class<?> clazz,LogCallback logCallback){
			log = org.apache.commons.logging.LogFactory.getLog(this.getClass());
			this.logCallback = logCallback;
		}
		public void trace(String format,Object ... args){
			if(!log.isTraceEnabled()) return;
			String message = StringUtil.format(format, args); 
			log.trace(message);
			logCallback.log(LogTrace.TRACE,message);
		}
		public void debug(String format,Object ... args){
			if(!log.isDebugEnabled()) return;
			String message = StringUtil.format(format, args);
			log.debug(message);
			logCallback.log(LogTrace.DEBUG,message);
		}
		public void info(String format,Object ... args){
			if(!log.isInfoEnabled()) return;
			String message = StringUtil.format(format, args);
			log.info(message);
			logCallback.log(LogTrace.INFO,message);
		}
		public void warn(String format,Object ... args){
			String message = StringUtil.format(format, args);
			log.warn(message);
			logCallback.log(LogTrace.WARN,message);
		}
		public void error(String format,Object ... args){
			String message = StringUtil.format(format, args);
			log.error(message);
		}
		public void debug(Object arg0, Throwable arg1) {
			log.debug(arg0, arg1);
		}
		public void debug(Object arg0) {
			log.debug(arg0);
		}
		public void error(Object arg0, Throwable arg1) {
			log.error(arg0, arg1);
		}
		public void error(Object arg0) {
			log.error(arg0);
		}
		public void fatal(Object arg0, Throwable arg1) {
			log.fatal(arg0, arg1);
		}
		public void fatal(Object arg0) {
			log.fatal(arg0);
		}
		public void info(Object arg0, Throwable arg1) {
			log.info(arg0, arg1);
		}
		public void info(Object arg0) {
			log.info(arg0);
		}
		public boolean isDebugEnabled() {
			return log.isDebugEnabled();
		}
		public boolean isErrorEnabled() {
			return log.isErrorEnabled();
		}
		public boolean isFatalEnabled() {
			return log.isFatalEnabled();
		}
		public boolean isInfoEnabled() {
			return log.isInfoEnabled();
		}
		public boolean isTraceEnabled() {
			return log.isTraceEnabled();
		}
		public boolean isWarnEnabled() {
			return log.isWarnEnabled();
		}
		public void trace(Object arg0, Throwable arg1) {
			log.trace(arg0, arg1);
		}
		public void trace(Object arg0) {
			log.trace(arg0);
		}
		public void warn(Object arg0, Throwable arg1) {
			log.warn(arg0, arg1);
		}
		public void warn(Object arg0) {
			log.warn(arg0);
		}
	}
	
	/** 아파치 로그에 템플릿 파라메터를 추가했다. */
	public static class DefaultLog implements Log2{
		private final Log log;
		private DefaultLog(Log log){
			this.log = log;
		}
		
		@Override
		public void trace(String format,Object ... args){
			if(!log.isTraceEnabled()) return;
			log.trace(StringUtil.format(format, args));
		}
		@Override
		public void debug(String format,Object ... args){
			if(!log.isDebugEnabled()) return;
			log.debug(StringUtil.format(format, args));
		}
		@Override
		public void info(String format,Object ... args){
			if(!log.isInfoEnabled()) return;
			log.info(StringUtil.format(format, args));
		}	
		@Override
		public void warn(String format,Object ... args){
			log.warn(StringUtil.format(format, args));
		}
		@Override
		public void error(String format,Object ... args){
			log.error(StringUtil.format(format, args));
		}

		@Override
		public void debug(Object arg0, Throwable arg1) {
			log.debug(arg0, arg1);
		}

		@Override
		public void debug(Object arg0) {
			log.debug(arg0);
		}

		@Override
		public void error(Object arg0, Throwable arg1) {
			log.error(arg0, arg1);
		}

		@Override
		public void error(Object arg0) {
			log.error(arg0);
		}

		@Override
		public void fatal(Object arg0, Throwable arg1) {
			log.fatal(arg0, arg1);
		}

		@Override
		public void fatal(Object arg0) {
			log.fatal(arg0);
		}

		@Override
		public void info(Object arg0, Throwable arg1) {
			log.info(arg0, arg1);
		}

		@Override
		public void info(Object arg0) {
			log.info(arg0);
		}

		@Override
		public boolean isDebugEnabled() {
			return log.isDebugEnabled();
		}

		@Override
		public boolean isErrorEnabled() {
			return log.isErrorEnabled();
		}

		@Override
		public boolean isFatalEnabled() {
			return log.isFatalEnabled();
		}

		@Override
		public boolean isInfoEnabled() {
			return log.isInfoEnabled();
		}

		@Override
		public boolean isTraceEnabled() {
			return log.isTraceEnabled();
		}

		@Override
		public boolean isWarnEnabled() {
			return log.isWarnEnabled();
		}

		@Override
		public void trace(Object arg0, Throwable arg1) {
			log.trace(arg0, arg1);
		}

		@Override
		public void trace(Object arg0) {
			log.trace(arg0);
		}

		@Override
		public void warn(Object arg0, Throwable arg1) {
			log.warn(arg0, arg1);
		}

		@Override
		public void warn(Object arg0) {
			log.warn(arg0);
		}
	}

}
