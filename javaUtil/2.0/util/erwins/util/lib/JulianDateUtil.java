package erwins.util.lib;
import java.util.Calendar;

/**
 * 기상청 같은데서 쓰이는구간이 있음.. 혹시나 해서 저장 
 * http://www.rgagnon.com/javadetails/java-0506.html */
public class JulianDateUtil {

	 /**
	  * Returns the Julian day number that begins at noon of
	  * this day, Positive year signifies A.D., negative year B.C.
	  * Remember that the year after 1 B.C. was 1 A.D.
	  *
	  * ref :
	  *  Numerical Recipes in C, 2nd ed., Cambridge University Press 1992
	  */
	//Gregorian Calendar adopted Oct. 15, 1582 (2299161)
	public static int JGREG = 15 + 31 * (10 + 12 * 1582);
	public static double HALFSECOND = 0.5;

	public static double toJulian(int[] ymd) {
		int year = ymd[0];
		int month = ymd[1]; // jan=1, feb=2,...
		int day = ymd[2];
		int julianYear = year;
		if (year < 0) julianYear++;
		int julianMonth = month;
		if (month > 2) {
			julianMonth++;
		} else {
			julianYear--;
			julianMonth += 13;
		}

		double julian = (java.lang.Math.floor(365.25 * julianYear) + java.lang.Math.floor(30.6001 * julianMonth) + day + 1720995.0);
		if (day + 31 * (month + 12 * year) >= JGREG) {
			// change over to Gregorian calendar
			int ja = (int) (0.01 * julianYear);
			julian += 2 - ja + (0.25 * ja);
		}
		return java.lang.Math.floor(julian);
	}
	
	public static double toJulian(Calendar calendar){
		return toJulian(new int[] { calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DATE) });
	}

	/**
	 * Converts a Julian day to a calendar date ref : Numerical Recipes in C,
	 * 2nd ed., Cambridge University Press 1992
	 */
	public static int[] fromJulian(double injulian) {
		int jalpha, ja, jb, jc, jd, je, year, month, day;
		double julian = injulian + HALFSECOND / 86400.0;
		ja = (int) julian;
		if (ja >= JGREG) {
			jalpha = (int) (((ja - 1867216) - 0.25) / 36524.25);
			ja = ja + 1 + jalpha - jalpha / 4;
		}

		jb = ja + 1524;
		jc = (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
		jd = 365 * jc + jc / 4;
		je = (int) ((jb - jd) / 30.6001);
		day = jb - jd - (int) (30.6001 * je);
		month = je - 1;
		if (month > 12) month = month - 12;
		year = jc - 4715;
		if (month > 2) year--;
		if (year <= 0) year--;

		return new int[] { year, month, day };
	}
	
}
