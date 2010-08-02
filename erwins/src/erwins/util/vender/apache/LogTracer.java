
package erwins.util.vender.apache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import erwins.util.root.DomainObject;
import erwins.util.root.EntityHibernatePaging;
import erwins.util.tools.InverseList;

public class LogTracer{
	
	private final List<String> loggerNames = new ArrayList<String>();
	
	private InverseList<LogTrace> stack;

	/** 필수로 호출해야 한다. */
	public void setMaxSize(int maxSize) {
		stack = new InverseList<LogTrace>(maxSize);
	}
	public void addLog(String className,String level,String log){
		stack.pushAndRemoveMaxValue(new LogTrace(className,level,log,new Date()));
	}
	
	public List<LogTrace> getLogTrace(){
		return stack;
	}
	
	public void registLoger(Log loger){
		loggerNames.add(loger.getClassName());
	}
	public List<String> getLoggerNames() {
		return loggerNames;
	}


	public class LogTrace implements EntityHibernatePaging,DomainObject{
		
		public static final String WARN = "WARN";
		public static final String INFO = "INFO";
		public static final String DEBUG = "DEBUG";
		public static final String TRACE = "TRACE";
		
		private final String className; 
		private final String level; 
		private final String log;
		private final Date createDate;
		private int rownum;
		
		public LogTrace(String className,String level,String log,Date createDate){
			this.className = className;
			this.level = level;
			this.log = log;
			this.createDate = createDate;
		}
		
		public String getLevel() {
			return level;
		}
		public String getClassName() {
			return className;
		}
		public String getLog() {
			return log;
		}
		public Date getCreateDate() {
			return createDate;
		}
		public int getRownum() {
			return rownum;
		}

		@Override
		public void setRownum(int rownum) {
			this.rownum = rownum;
		}
		
	}

}
