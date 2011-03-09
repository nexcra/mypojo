package erwins.util.valueObject;

import java.util.Calendar;
import java.util.Date;

import erwins.util.lib.Days;
import erwins.util.lib.Strings;

/**
 * yyyyMMdd 스타일의 8자리 일자를 나타낸다. 불변객체 아님. ㅠㅠ
 */
public class Day implements ValueObject,Comparable<Day> {

	public static Day now() {
		String yyyyMMdd = Days.DATE_FOR_DB.get();
		return new Day(yyyyMMdd);
	}

	/** 일/월/년을 각각 더한다. */
	public void plus(Integer... time) {
		Calendar thisCalendar = Days.getCalendar(returnValue().toString());
		Calendar added = Days.addCalendar(thisCalendar, time);
		String yyyyMMdd = Days.DATE_FOR_DB.get(added);
		initValue(yyyyMMdd);
	}

	public Day() {};

	public Day(Object obj) {
		initValue(obj);
	}

	private String day;
	private String month;
	private String year;

	/** A/B/C 스타일이 더 나은듯. */
	@Override
	public String toString() {
		return year + "년" + month + "월" + day + "일";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ValueObject))
			return false;
		ValueObject other = (ValueObject) obj;
		if(this.returnValue()==null) return false;
		if(other.returnValue()==null) return false;
		if(this.returnValue().toString().equals(other.returnValue().toString())) return true;
		return false;
	}

	@Override
	public Object returnValue() {
		return year + month + day;
	}

	@Override
	public void initValue(Object obj) {
		if(obj instanceof Date) obj = Days.DATE_FOR_DB.get((Date)obj);
		String yyyyMMdd = Strings.getNumericStr(obj);
		if (yyyyMMdd.length() != 8) throw new ValueObjectBuildException(obj,yyyyMMdd + " : day lenth must be 8!");
		year = yyyyMMdd.substring(0, 4);
		month = yyyyMMdd.substring(4, 6);
		day = yyyyMMdd.substring(6, 8);
	}

	public String getDay() {
		return day;
	}

	public String getMonth() {
		return month;
	}

	public String getYear() {
		return year;
	}

	/** 더 큰가? null이거나 동일할 경우 기본값은 false이다. */
	public boolean isLarge(Day o) {
		return this.isLarge(o, false);
	}
	
	/** 더 큰가? */
	public boolean isLarge(Day o,boolean same) {
		if(o==null) return same;
		int compare = this.compareTo(o);
		if(compare==0) return same;
		return compare > 0; 
	}

	@Override
	public int compareTo(Day o) {
		return this.returnValue().toString().compareTo(o.returnValue().toString());
	}

}