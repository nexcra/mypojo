package erwins.util.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;

/** 글로벌 스탠다드한 Date 보조객체  ~ 아직 안만듬.
 * 프로젝트별로 이 클래스를 확장해서 DateTimeFormatter 가 있는 클래스를 만들어 사용하자
 * http://joda-time.sourceforge.net/ */
public abstract class JodaTimeUtil{
	
	/** 시작부터 size만큼 field를 증가시켜 리스트로 반환한다. size가 1이면 list의 size()는 2이다.
	 * ex) List<DateTime> result =  JodaTimeUtil.toList(new DateTime(),Period.days(1),5); */
	public static List<DateTime> toList(DateTime start,Period period,int size) {
		List<DateTime> list = new ArrayList<DateTime>();
		list.add(start);
		MutableDateTime current =  start.toMutableDateTime();
		for(int i=0;i < size;i++){
			current.add(period);
			list.add(current.toDateTime());
		}
		return list;
	}
	
	public boolean isAfterPayDay(DateTime datetime) {
		if (datetime.getMonthOfYear() == 2) { // February is month 2!!
			return datetime.getDayOfMonth() > 26;
		}
		return datetime.getDayOfMonth() > 28;
	}

	public Days daysToNewYear(LocalDate fromDate) {
		LocalDate newYear = fromDate.plusYears(1).withDayOfYear(1);
		return Days.daysBetween(fromDate, newYear);
	}

	public boolean isRentalOverdue(DateTime datetimeRented) {
		Period rentalPeriod = new Period().withDays(2).withHours(12);
		return datetimeRented.plus(rentalPeriod).isBeforeNow();
	}

	public String getBirthMonthText(LocalDate dateOfBirth) {
		return dateOfBirth.monthOfYear().getAsText(Locale.KOREAN);
	}

}
