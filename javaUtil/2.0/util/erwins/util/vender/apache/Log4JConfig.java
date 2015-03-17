
package erwins.util.vender.apache;

import java.io.File;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import erwins.util.lib.FileUtil;


/** 
 * Log4J의 log4j.properties를 대체하기 위한 간이 설정기
 * 초기화 하기 전까지는 기본 로그들이 튀어나온다.. 별 신경쓰지 않을거면 쓰자. 
 *  */
public class Log4JConfig{
	
	/** 
	#1) C : 로그메시지를 기록하려는 클래스의 이름 출력! 패키지 계층 제어 가능 ex) %C{2}
	#2) d : 메시지 기록 시간 출력! 포멧 지정 가능! 포멧은 java.text.SimpleDateFormat과 같은 포맷 ex) %d{yyyy-MM-dd HH:mm:ss}
	#3) p : 로그 메시지의 우선순위 출력
	#4) m : 로그 메시지 자체를 출력
	#5) M : 로그 메시지를 기록하려는 메소드의 이름 출력
	#6) n : 플렛폼의 라인 구분자를 출력
	#7) % : %%는 '%'자체를 출력 */
	public static final String DEFAULT_PATTERTN = "[%5p] [%d{yyyy-MM-dd HH:mm}] %c : %m %n";
	
	/** 
	 * 시분 까지 입력하면 해당 단위로 롤링된다.
	 * yyyy-ww	매주의 시작시 로그파일을 변경합니다.
	   yyyy-MM-dd-a	자정과 정오에 로그파일을 변경합니다.
	 *  */
	public static final String DEFAULT_DATE_PATTERTN = ".yyyy-MM-dd'.log'";
	
	private static boolean init = false;
	
	/** 
	 * STEP01 : 로그 사용전 초기화
	 * ex) Log4JConfig.configRootAndConsole(Level.WARN,Log4JConfig.DEFAULT_PATTERTN);
	 *  */
	public static void configRootAndConsole(Level level,String logPattern){
		Preconditions.checkState(!init,"초기화가 이미 되있습니다.");
		init = true;	
		Logger root = Logger.getRootLogger();
		root.setLevel(level);
		root.removeAllAppenders(); //디폴트로 들어가있는 애들이 있다.. 뭔지 모르겠지만 걍 다 지운다.
		
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setName("default console appender");
		consoleAppender.setLayout(new PatternLayout(logPattern));
		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}
	
	/** 
	 * STEP02 : 루트 초기화 후 개별 로거 레벨 설정
	 * ex) Log4JConfig.configLoggerLevel("erwins.util.vender.mybatis.QueryStatisticsMybatisInterceptor",Level.DEBUG); //SQL 로그
	 * */
	public static void configLoggerLevel(String path,Level level){
		Logger.getLogger(path).setLevel(level);
	}
	
	/** 
	 * STEP03 : 추가로 사용할 파일 어펜더 설정
	 * ex) Log4JConfig.addRollingFileAppender(logDir, "warn.log", Level.WARN,Log4JConfig.DEFAULT_PATTERTN ,Log4JConfig.DEFAULT_DATE_PATTERTN,"com.epe");
	 * 해당 paht 기준 threshold 이상의 모든 로그들이 해당 파일에 기록된다.
	 * */
	@SuppressWarnings("unchecked")
	public static void addRollingFileAppender(File dir,String filename,Level threshold,String logPattern,String datePattern,String path){
		FileUtil.mkdirOrThrowException(dir);
		File logFile = new File(dir,filename);
		String appenderName = filename + " " + threshold + " rollingFileAppender";
		
		DailyRollingFileAppender fileAppender = new DailyRollingFileAppender();
		fileAppender.setName(appenderName);
		fileAppender.setFile(logFile.getAbsolutePath());
		fileAppender.setLayout(new PatternLayout(DEFAULT_PATTERTN));
		fileAppender.setAppend(true);
		fileAppender.setThreshold(threshold);
		fileAppender.setDatePattern(datePattern);
		fileAppender.setEncoding("UTF-8");
		fileAppender.activateOptions();
		
		Logger logger = Strings.isNullOrEmpty(path) ? Logger.getRootLogger() : Logger.getLogger(path);
		
		//중복방지 체크
		Enumeration<Appender> appenders = logger.getAllAppenders();
		while(appenders.hasMoreElements()){
			Appender appender = appenders.nextElement();
			Preconditions.checkState(!appenderName.equals(appender.getName()));
		}
		
		logger.addAppender(fileAppender);
		
	}
	
	
	

}
