package erwins.util.lib;

import java.io.File;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;


/** 기본 slf4j를 사용할것~ */
public abstract class Log4jUtil {
    
    /** 로그파일 위치를 수정해준다.
     * 일단 파일라이터를 기본으로 생성하게 한 후 어펜더의 파일위치만 수정한다.
     * 로컬과 서버의 로깅파일 위치를 다르게 하고싶을때 사용하자.
     * 상대경로로 설정했을경우 기본은 java의 경로가 root이다. (이클립스 서버스로 띄울경우 java가 설치된곳) 
     * --> 결국 뻘짓인듯.. ㅠㅠ  log4j설정에서 jvm파라메터를 받으니 그걸로 할것! */
    public static void renameRootLogFile(String appendername,String newPath){
        Logger logger = Logger.getRootLogger();
        Appender appender = logger.getAppender(appendername);
        if(appender==null) throw new RuntimeException("해당 이름의 어펜더가 없습니다 : " + appendername);
        if(! FileAppender.class.isInstance(appender) ) throw new RuntimeException("어펜더는 FileAppender만 가능합니다 : " + appendername);
        FileAppender ap =  (FileAppender)logger.getAppender(appendername);
        ap.setFile(newPath);
    }
    
    /** loggerName이 없으면 루트로거 */
    public static File findLogFile(String loggerName){
        Logger logger = loggerName==null ? Logger.getRootLogger() :  Logger.getLogger(loggerName);
        if(logger==null) throw new RuntimeException("해당 이름의 로거가 없습니다 : " + loggerName);
        @SuppressWarnings("unchecked")
        Enumeration<Appender> e = logger.getAllAppenders();
        FileAppender fileAppender = null;
        while(e.hasMoreElements()){
            Appender appender = e.nextElement();
            if(FileAppender.class.isInstance(appender) ) fileAppender = (FileAppender) appender;
        }
        if(fileAppender==null) throw new RuntimeException("해당 로거에 파일어펜더가 존재하지 않습니다." + loggerName);
        String filePath = fileAppender.getFile();
        if(filePath==null) throw new RuntimeException("해당 어펜더에 파일로거가 지정되지 않았습니다" + loggerName);
        return new File(filePath);
    }

}
