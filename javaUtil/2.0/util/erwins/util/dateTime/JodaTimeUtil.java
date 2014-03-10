package erwins.util.dateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/**
 * 글로벌 스탠다드한 Date 보조객체 ~ 아직 안만듬. 프로젝트별로 이 클래스를 확장해서 DateTimeFormatter 가 있는 클래스를
 * 만들어 사용하자 http://joda-time.sourceforge.net/
 */
public abstract class JodaTimeUtil {

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
	public static DateTime endTimeOfDay(DateMidnight startDate){
	    return startDate.plusDays(1).toDateTime().minusMillis(1);
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
    	DateMidnight start = JodaUtil.toDateMidnight(startDate);
    	DateMidnight end = JodaUtil.toDateMidnight(endDate);
    	//크기 비교는 안함
    	List<DateTime> between = JodaUtil.between(start, end, Period.days(1));
    	return FluentIterable.from(between).transform(JodaTimeUtil.formatFuction(JodaUtil.YMD));
    }
    
    

}
