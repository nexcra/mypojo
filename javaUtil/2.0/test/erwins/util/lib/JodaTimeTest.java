package erwins.util.lib;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Test;

/** 오픈소스 활용 저장용
 * DateTime이 도메인객체에 직접 사용된다면 활용도가 좋을듯하나..
 * 일반SI에서 썩 좋아보이진 않는다.
 * http://joda-time.sourceforge.net/ */
public class JodaTimeTest{

	@Test
	public void t1() throws Exception {
		LocalDate asd = new LocalDate();
		DateTime qq = new DateTime();
		
		LocalDate newYear = asd.plusYears(1).withDayOfYear(33);
		System.out.println(newYear);
		
		System.out.println(asd);
		System.out.println(qq);
		
		System.out.println(daysToNewYear(asd).getDays());
		
		// DateTime time = new DateTime;
		System.out.println(asd.monthOfYear().getAsText(Locale.KOREAN));
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
