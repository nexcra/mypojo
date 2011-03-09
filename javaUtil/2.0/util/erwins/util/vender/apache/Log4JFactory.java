
package erwins.util.vender.apache;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import erwins.util.lib.SystemUtil;


/** Log4J의 log4j.properties를 대체하기 위한 클래스팩토리.
	#1) C : 로그메시지를 기록하려는 클래스의 이름 출력! 패키지 계층 제어 가능 ex) %C{2}
	#2) d : 메시지 기록 시간 출력! 포멧 지정 가능! 포멧은 java.text.SimpleDateFormat과 같은 포맷 ex) %d{yyyy-MM-dd HH:mm:ss}
	#3) p : 로그 메시지의 우선순위 출력
	#4) m : 로그 메시지 자체를 출력
	#5) M : 로그 메시지를 기록하려는 메소드의 이름 출력
	#6) n : 플렛폼의 라인 구분자를 출력
	#7) % : %%는 '%'자체를 출력 */
public class Log4JFactory{
	
	public static final String DEFAULT_PATTERTN = "[%5p] [%d{yyyy-MM-dd HH:mm}] %c : %m %n";
	public static final String DEFAULT_DATE_PATTERTN = ".yyyy-MM-dd";
	
	private final Appender consoleAppender;
	private final Layout patternLayout;
	private Level level = SystemUtil.IS_OS_WINDOWS ? Level.DEBUG : Level.INFO ;
	
	public Log4JFactory(){
		this.patternLayout = new PatternLayout(DEFAULT_PATTERTN);
		this.consoleAppender = new ConsoleAppender(patternLayout);
		consoleAppender.setName("console");
	}
	
	/** %p	debug, info, warn, error, fatal 등의 priority 가 출력된다.
		%m	로그내용이 출력됩니다
		%d	로깅 이벤트가 발생한 시간을 기록합니다. 포맷은 %d{HH:mm:ss, SSS}, %d{yyyy MMM dd HH:mm:ss, SSS}같은 형태로 사용하며 SimpleDateFormat에 따른 포맷팅을 하면 된다
		%t	로그이벤트가 발생된 쓰레드의 이름을 출력합니다.
		%%	% 표시를 출력하기 위해 사용한다.
		%n	플랫폼 종속적인 개행문자가 출력된다. \r\n 또는 \n 일것이다.
		%c	카테고리를 표시합니다 예) 카테고리가 a.b.c 처럼 되어있다면 %c{2}는 b.c가 출력됩니다.
		%C	클래스명을 포시합니다. 예) 클래스구조가 org.apache.xyz.SomeClass 처럼 되어있다면 %C{2}는 xyz.SomeClass 가 출력됩니다
		%F	로깅이 발생한 프로그램 파일명을 나타냅니다.
		%l	로깅이 발생한 caller의 정보를 나타냅니다
		%L	로깅이 발생한 caller의 라인수를 나타냅니다
		%M	로깅이 발생한 method 이름을 나타냅니다.
		%r	어플리케이션 시작 이후 부터 로깅이 발생한 시점의 시간(milliseconds)
		%x	로깅이 발생한 thread와 관련된 NDC(nested diagnostic context)를 출력합니다.
		%X	로깅이 발생한 thread와 관련된 MDC(mapped diagnostic context)를 출력합니다.
	 *  */
	public Log4JFactory(String pattern) {
		this.patternLayout = new PatternLayout(pattern);
		this.consoleAppender = new ConsoleAppender(patternLayout);
		consoleAppender.setName("console");
	}
	private Appender fileAppender;
	private File logDirectory;
	/** 로그파일명 포맷 (DatePattern)
		로그파일명 포맷입니다. 날짜, 시간 및 분단위로까지 로그 파일을 분리할 수 있습니다.
		'.'yyyy-MM	매달 첫번째날에 로그파일을 변경합니다
		'.'yyyy-ww	매주의 시작시 로그파일을 변경합니다.
		'.'yyyy-MM-dd	매일 자정에 로그파일을 변경합니다.
		'.'yyyy-MM-dd-a	자정과 정오에 로그파일을 변경합니다.
		'.'yyyy-MM-dd-HH	매 시간의 시작마다 로그파일을 변경합니다.
		'.'yyyy-MM-dd-HH-mm	매분마다 로그파일을 변경합니다. 
	 *  */
	public void addFileAppender(File logDirectory, String fileName,String datePattern) {
		this.logDirectory = logDirectory;
		File fullPath = new File(logDirectory,fileName);
		try {
			this.fileAppender = new DailyRollingFileAppender(patternLayout, fullPath.getAbsolutePath(), datePattern);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level){
		this.level = level;
	}
	/** 로그파일을 조회 후 다운로드할때 사용한다. 편의기능을 위한것임. */
	public File getLogDirectory() {
		return logDirectory;
	}
	public Logger instance(String name){
		Logger log = Logger.getLogger(name);
		log.addAppender(consoleAppender);
		if(fileAppender!=null) log.addAppender(fileAppender);
		log.setLevel(level);
		return log;
	}
	
	/** root가 있는지 (즉 log4j.properties설정이 있는지) 확인후 있다면 어펜더 리턴 */
	@SuppressWarnings("rawtypes")
	public static Appender root(){
		Logger root = Logger.getRootLogger();
		Enumeration e =  root.getAllAppenders();
		if(e==null || !e.hasMoreElements()) return null;
		Appender rootAppender = (Appender)e.nextElement();
		return rootAppender; 
	}

}
