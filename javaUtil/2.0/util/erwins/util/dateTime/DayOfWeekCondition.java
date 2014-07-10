
package erwins.util.dateTime;

import org.joda.time.DateTime;


/** 7자리 숫자로 일월화수목금토을 나타낼때 사용된다. 숫자는1,0으로 true / false를 나타낸다. */
public class DayOfWeekCondition extends AbstractStringBooleanCondition{

	public DayOfWeekCondition(String dayStr) {
		super(dayStr);
	}

	@Override
	protected int getSize() {
		return 7;
	}
	
	public boolean isAble(DateTime date){
		return condition[date.getDayOfWeek()];
	}
	
}