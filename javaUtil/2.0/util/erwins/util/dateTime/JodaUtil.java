package erwins.util.dateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.AbstractInstant;
import org.joda.time.base.AbstractPartial;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/** 
 * http://joda-time.sourceforge.net/
 * */
@ThreadSafe
public abstract class JodaUtil{

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
    /** 년월일시  */
    public static DateTimeFormatter YMDH = DateTimeFormat.forPattern("yyyyMMddHH");
    /** 년월일 */
    public static DateTimeFormatter YMD = DateTimeFormat.forPattern("yyyyMMdd");
    /** 시분 */
    public static DateTimeFormatter YM = DateTimeFormat.forPattern("yyyyMM");
    public static DateTimeFormatter Y = DateTimeFormat.forPattern("yyyy");
    public static DateTimeFormatter HM = DateTimeFormat.forPattern("HHmm");
    public static DateTimeFormatter M = DateTimeFormat.forPattern("mm");
    
    /** 파싱용 간단 객체 */
    public enum Joda{
    	DATE_KR(JodaUtil.DATE_KR),
    	TIME_KR(JodaUtil.TIME_KR),
    	YMDHMSS(JodaUtil.YMDHMSS),
    	YMDHMS(JodaUtil.YMDHMS),
    	YMDHM(JodaUtil.YMDHM),
    	YMDH(JodaUtil.YMDH),
    	YMD(JodaUtil.YMD),
    	YM(JodaUtil.YM),
    	M(JodaUtil.M),
    	;
    	public final DateTimeFormatter format;

		private Joda(DateTimeFormatter format) {
			this.format = format;
		}
		public String get(){
			return new DateTime().toString(format);
		}
		public String get(AbstractInstant dateTime){
			return dateTime.toString(format);
		}
		public String get(AbstractPartial dateTime){
			return dateTime.toString(format);
		}
		/** 숫자 형식만 남겨서 파싱한다. */
		public DateTime get(String dateTime){
			String escaped = CharMatcher.DIGIT.retainFrom(dateTime); 
			return format.parseDateTime(escaped);
		}
    }
    
	/** 어제일자 반환 */
	public static String yesterday(){
	    return new LocalDate().minusDays(1).toString(JodaUtil.YMD);
	}
	/** 어제일자 반환 */
	public static String today(){
	    return new LocalDate().toString(JodaUtil.YMD);
	}
	
    /** 년월일 형식을 리턴한다.  */
    public static LocalDate toLocalDate(String yyyyMMdd){
    	String escaped = CharMatcher.DIGIT.retainFrom(yyyyMMdd); 
		return JodaUtil.YMD.parseDateTime(escaped).toLocalDate();
    }
    
    /**
	 * 시작부터 size만큼 field를 증가시켜 리스트로 반환한다. size가 1이면 list의 size()는 2이다. ex)
	 * List<DateTime> result = JodaTimeUtil.toList(new
	 * DateTime(),Period.days(1),5);
	 */
	public static List<DateTime> toList(DateTime start, Period period, int size) {
		List<DateTime> list = new ArrayList<DateTime>();
		list.add(start);
		MutableDateTime current = start.toMutableDateTime();
		for (int i = 0; i < size; i++) {
			current.add(period);
			list.add(current.toDateTime());
		}
		return list;
	}

	public Days daysToNewYear(LocalDate fromDate) {
		LocalDate newYear = fromDate.plusYears(1).withDayOfYear(1);
		return Days.daysBetween(fromDate, newYear);
	}

	/** run에 걸린 시간을 리턴한다 */
	public long simpleTime(Runnable run) {
		long start = System.currentTimeMillis();
		run.run();
		long end = System.currentTimeMillis();
		return end - start;
	}
	
    /** 시간차를 초단위로 리턴한다. */
    public static int interval(Date aa,Date bb) {
        long interval = aa.getTime() - bb.getTime();
        return (int)interval / 1000;
    }
    
	/** 하루를 더한 후 1밀리초 뺀다 */
	public static DateTime endTimeOfDay(LocalDate startDate){
	    return startDate.plusDays(1).toDateTimeAtCurrentTime().minusMillis(1);
	}
	
    /** 동일시간 포함, period 간격 만큼의 DateTime을 리턴한다.
     * ex) List<DateTime> times = between(start,end,Period.days(1));
     * 년월일의 경우 시작,종료 일자가 포함된다.  */
    public static List<DateTime> between(BaseDateTime start,BaseDateTime end,ReadablePeriod period){
    	Preconditions.checkArgument(start.isBefore(end) || start.isEqual(end),"start는 end보다 작거나 같아야 합니다");
    	DateTime startTime = start.toDateTime();
    	List<DateTime> times = Lists.newArrayList();
    	
    	while(startTime.isBefore(end) || startTime.isEqual(end)){
    		times.add(startTime);
    		startTime = startTime.plus(period);
    	}
    	return times;
    }
    
    /** 배열을 간단하게 포매팅 할때 사용된다. 
     * ex) List<String> dates =  FluentIterable.from(times).transform(formatFuction(JodaUtil.YMD)).toList(); */
    public static Function<BaseDateTime,String> formatFuction(final DateTimeFormatter formatter){
    	return new Function<BaseDateTime,String>(){
			@Override
			public String apply(BaseDateTime arg0) {
				return arg0.toString(formatter);
			}
    	};
    }
    
    /** 위를 이용한 샘플  .toSet() 등을 하길 바람 */
    public static FluentIterable<String> betweenDate(String startDate,String endDate){
    	Preconditions.checkState(!Strings.isNullOrEmpty(startDate),"startDate is required");
    	Preconditions.checkState(!Strings.isNullOrEmpty(endDate),"endDate is required");
    	LocalDate start = JodaUtil.toLocalDate(startDate);
    	LocalDate end = JodaUtil.toLocalDate(endDate);
    	//크기 비교는 안함
    	List<DateTime> between = JodaUtil.between(start.toDateTimeAtCurrentTime(), end.toDateTimeAtCurrentTime(), Period.days(1));
    	return FluentIterable.from(between).transform(formatFuction(JodaUtil.YMD));
    }
    
    /** 
     * 까먹을까봐 여기 정리. 남는값은 버림한다.
     * "일" 로 trim()하면 new LocalDate() 와 동일해진다.
     * new LocalDate().toDate() == trim(dateTime,DateTimeFieldType.dayOfYear())
     *  */
    public static DateTime trim(DateTime dateTime,DateTimeFieldType type){
    	return dateTime.property(type).roundFloorCopy();
    }
    
    
}
