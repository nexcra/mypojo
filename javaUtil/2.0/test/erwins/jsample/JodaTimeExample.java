package erwins.jsample;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;


/** 
 * http://joda-time.sourceforge.net/quickstart.html  참고
 * Interval : 1/1000초 단위의 Instant 들 간의 시간 차이. 물론, TimeZone 기반입니다.
 * Duration : TimeZone을 가지지 않는 시간의 길이 (1/1000)단위 Interval로 부터 정보를 얻을 수 있다.
 * Period(기간) TimeZone을 가지지 않는 시간의 길이 (1/1000)단위  특정 필드로 표현된다. 필드 (Period, Years, Months, Weeks, Days, Hours, Minutes, Seconds)
 * 
 * DateTimeFormat 이 스레드 세이프하다 @_@
 * 일자계산 많이해야할때는 MutableDateTime 사용
 *  ISO standard format for datetime, which is yyyy-MM-dd'T'HH:mm:ss.SSSZZ  -> 생성자에 이동할 수 있다.
 *  */ 
public class JodaTimeExample{
    
	DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	
	/** 기본기 */
	//@Test
    public void test(){
		
		DateTime dt = new DateTime();
		System.out.println(dt.toString(fmt));
		DateTime result1 = dt.dayOfWeek().setCopy(DateTimeConstants.MONDAY); ///월요일로 변신
		DateTime result2 = dt.dayOfWeek().addToCopy(2); ///월요일로 변신
		System.out.println(result1.toString(fmt));
		System.out.println(result2.toString(fmt));
		System.out.println(fmt.print(result2));
		
		System.out.println(dt.dayOfYear().getMaximumValue());
		System.out.println(dt.dayOfMonth().getMaximumValue());
		
		DateTime year2000 = dt.withYear(2000);
		DateTime twoHoursLater = dt.plusHours(2);
		
		String monthName = dt.monthOfYear().getAsText(Locale.KOREA); //Properties사용
		System.out.println(monthName);
		dt.year().isLeap(); //윤연?
		DateTime rounded = dt.dayOfMonth().roundFloorCopy();
		System.out.println(rounded);
		
		//생성자
		DateTime temp1 = new DateTime("2012-03-12");
		DateTime temp2 = new DateTime(new Date());
		
		System.out.println(temp1);
		System.out.println(temp2);
		
    }
	
	//@Test
	public void Interval(){
		DateTime aTime = new DateTime("2005-11-05");
		DateTime bTime = new DateTime();
		
		System.out.println(Days.daysBetween(aTime, bTime).getDays());
		System.out.println(Weeks.weeksBetween(aTime, bTime).getWeeks());
		System.out.println(Months.monthsBetween(aTime, bTime).getMonths());
		 
		Interval interval = new Interval(aTime, bTime);
		System.out.println(interval.toDuration().getMillis());
		System.out.println(interval.toPeriod(PeriodType.days()).getDays());
	}
	
	
	@Test
	public void Duration(){
		DateTime start = new DateTime(2004, 12, 25, 0, 0, 0, 0);
		DateTime end = new DateTime(2005, 1, 1, 0, 0, 0, 0);
		Duration dur = new Duration(start, end);
		
		DateTime calc = start.plus(dur);
		Assert.assertTrue(calc.equals(end));
	}
	
	
	
}