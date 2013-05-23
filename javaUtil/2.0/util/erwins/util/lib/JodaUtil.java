package erwins.util.lib;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.CharMatcher;

@ThreadSafe
public abstract class JodaUtil extends JodaTimeUtil {

    //=======================  예쁘게 찍는 포매터 모음집  ================================  
    public static DateTimeFormatter TIME1 = DateTimeFormat.forPattern("yyyy MM-dd HH:mm:ss");
    public static DateTimeFormatter TIME_KR = DateTimeFormat.forPattern("yyyy년MM월dd일(EEE) HH시mm분ss초");
    
    public static DateTimeFormatter DATE1 = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static DateTimeFormatter DATE_KR = DateTimeFormat.forPattern("yyyy년MM월dd일(EEE)");

    //=======================  DB or 파싱용 포매터 모음집   ================================
    /** 밀리초까지 사용하는 17자리 수 */
    public static DateTimeFormatter YMDHMSS = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
    /** 자바스크립트 기본(서버시간때문에 long을 사용하지 않는다.). 클라이언트에서 역으로 파싱해서 사용하자 */
    public static DateTimeFormatter YMDHMS = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    /** 년월일시분  */
    public static DateTimeFormatter YMDHM = DateTimeFormat.forPattern("yyyyMMddHHmm");
    /** 년월일 */
    public static DateTimeFormatter YMD = DateTimeFormat.forPattern("yyyyMMdd");
    /** 시분 */
    public static DateTimeFormatter YM = DateTimeFormat.forPattern("yyyyMM");
    public static DateTimeFormatter HM = DateTimeFormat.forPattern("HHmm");
    
	/** 어제일자 반환 */
	public static String yesterday(){
	    return new DateMidnight().minusDays(1).toString(JodaUtil.YMD);
	}
	/** 어제일자 반환 */
	public static String today(){
	    return new DateMidnight().toString(JodaUtil.YMD);
	}
	
	/** 트랜잭션 타임  */
    public static String regTime(){
        return new DateTime().toString(JodaUtil.YMDHMSS);
    }
    
    /** 년월일 형식을 리턴한다.  */
    public static DateMidnight toDateMidnight(String yyyyMMdd){
    	String escaped = CharMatcher.DIGIT.retainFrom(yyyyMMdd); 
		return JodaUtil.YMD.parseDateTime(escaped).toDateMidnight();
    }
    
}
