package erwins.util.spring.web.conversion;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Printer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import erwins.util.dateTime.JodaUtil.Joda;


/** 
 * 컨버전 서비스에 들어가는 컨버터 모음.
 * 크게 어노테이션 컨버터(특정 필드에만 적용)와 일반 컨버터(글로벌 적용)로 구분된다.
 * ex) addConverter(ConversionSet.TO_DATE);
	   addFormatterForFieldAnnotation(new StringFormatFactory());
 * 여기는 일반 컨버터(글로벌 적용)을 기술한다.
 *  */
public abstract class ConversionSet {
	
	public static final Converter<String,Date> TO_DATE =  new Converter<String,Date>() {
		@Override
		public Date convert(String timeStr) {
			DateTime time = TO_DATETIME.convert(timeStr);
			return time==null ? null : time.toDate();
		}
	};
	
	public static final Converter<String,DateTime> TO_DATETIME =  new Converter<String,DateTime>() {
		@Override
		public DateTime convert(String timeStr) {
			if(timeStr==null) return null;
			String time = CharMatcher.DIGIT.retainFrom(timeStr);
			if(Strings.isNullOrEmpty(time)) return null;
			int timeLength = time.length();
			if(timeLength==6){
				return Joda.YM.get(time);
			}else if(timeLength==8){
				return Joda.YMD.get(time);
			}else if(timeLength==10){
				return Joda.YMDH.get(time);
			}else if(timeLength==12){
				return Joda.YMDHM.get(time);
			}else if(timeLength==14){
				return Joda.YMDHMS.get(time);
			}else if(timeLength==17){
				return Joda.YMDHMSS.get(time);
			}
			throw new IllegalArgumentException(time +" 는 파싱할 수 없는 DateTime 타입입니다");
		}
	};
	
	/** 먹은거 그대로 싼다 */
	public static Printer<String> PASS_THROUGH_PRINTER = new Printer<String>(){

		@Override
		public String print(String input, Locale arg1) {
			return input;
		}
		
	};
	
	
}
