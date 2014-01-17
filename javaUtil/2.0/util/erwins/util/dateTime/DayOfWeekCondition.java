
package erwins.util.dateTime;

import java.util.regex.Pattern;

import org.joda.time.DateTime;

import erwins.util.text.RegEx;

/** 7자리 숫자로 일월화수목금토을 나타낼때 사용된다. 숫자는1,0으로 true / false를 나타낸다. */
public class DayOfWeekCondition{
	
	private final boolean[] dayOfWeekAble = new boolean[7];
	public DayOfWeekCondition(String dayStr){
		if(dayStr.length() != 7) throw new IllegalArgumentException("7자리 1,0만 허용됩니다." + dayStr);
		if(!RegEx.isFullMatch(Pattern.compile("[01]*"), dayStr)) throw new IllegalArgumentException("7자리 1,0만 허용됩니다." + dayStr);
		for(int i=0;i<7;i++){
			dayOfWeekAble[i] = dayStr.substring(i,i+1).equals("1");
		}
	}
	public boolean isAble(int dow){
		return dayOfWeekAble[dow];
	}
	public boolean isAble(DateTime date){
		return dayOfWeekAble[date.getDayOfWeek()];
	}
}