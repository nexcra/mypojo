package erwins.util.lib;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import erwins.util.lib.JodaTimeUtil;

public abstract class JodaUtil extends JodaTimeUtil {
	
	//=======================  예쁘게 찍는 포매터 모음집  ================================  
	public static DateTimeFormatter TIME1 = DateTimeFormat.forPattern("yyyy MM-dd HH:mm:ss");
	public static DateTimeFormatter TIME_KR = DateTimeFormat.forPattern("yyyy년MM월dd일(EEE) HH시mm분ss초");
	
	public static DateTimeFormatter DATE1 = DateTimeFormat.forPattern("yyyy-MM-dd");
	public static DateTimeFormatter DATE_KR = DateTimeFormat.forPattern("yyyy년MM월dd일(EEE)");

	//=======================  DB or 파싱용 포매터 모음집   ================================  
	/** 자바스크립트 기본(서버시간때문에 long을 사용하지 않는다.). 클라이언트에서 역으로 파싱해서 사용하자 */
	public static DateTimeFormatter YMDHMS = DateTimeFormat.forPattern("yyyyMMddHHmmss");
	public static DateTimeFormatter YMD = DateTimeFormat.forPattern("yyyyMMdd");
    
}
