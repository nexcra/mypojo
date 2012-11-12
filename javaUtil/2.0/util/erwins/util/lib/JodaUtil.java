package erwins.util.lib;

import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@ThreadSafe
public abstract class JodaUtil extends JodaTimeUtil {
	
	//=======================  예쁘게 찍는 포매터 모음집  ================================  
	public static DateTimeFormatter TIME1 = DateTimeFormat.forPattern("yyyy MM-dd HH:mm:ss");
	public static DateTimeFormatter TIME_KR = DateTimeFormat.forPattern("yyyy년MM월dd일(EEE) HH시mm분ss초");
	
	public static DateTimeFormatter DATE1 = DateTimeFormat.forPattern("yyyy-MM-dd");
	public static DateTimeFormatter DATE_KR = DateTimeFormat.forPattern("yyyy년MM월dd일(EEE)");

	//=======================  DB or 파싱용 포매터 모음집   ================================  
	/** 년월일시분초 :  자바스크립트 기본(서버시간때문에 long을 사용하지 않는다.). 클라이언트에서 역으로 파싱해서 사용하자 */
	public static DateTimeFormatter YMDHMS = DateTimeFormat.forPattern("yyyyMMddHHmmss");
	/** 년월일시분  */
    public static DateTimeFormatter YMDHM = DateTimeFormat.forPattern("yyyyMMddHHmm");
    /** 년월일 */
    public static DateTimeFormatter YMD = DateTimeFormat.forPattern("yyyyMMdd");
    /** 시분 */
    public static DateTimeFormatter HM = DateTimeFormat.forPattern("HHmm");
    
    /** 시간차를 초단위로 리턴한다. */
    public static int interval(Date aa,Date bb) {
        long interval = aa.getTime() - bb.getTime();
        return (int)interval / 1000;
    }
    
}
