
package erwins.util.dateTime;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import erwins.util.text.StringUtil;


/** 24자리 숫자로 1~24시를  나타낼때 사용된다. 숫자는1,0으로 true / false를 나타낸다.
	정오부터 새벽1시가 인덱스0이다 */
public class HourOfDayCondition extends AbstractStringBooleanCondition{

	public HourOfDayCondition(String dayStr) {
		super(dayStr);
	}
	
	/** ex) "1~4,3,7,8,11~34" */
	public static String parseToHourOfDayCondition(String text){
		Set<Long> set = StringUtil.parseStringToLongSet(text);
		List<String> hourBoolean = Lists.newArrayList();
		for(long i=0;i<24;i++){
			hourBoolean.add(set.contains(Long.valueOf(i)) ? "1" : "0" );
		}
		return StringUtil.join(hourBoolean, "");
	}

	@Override
	protected int getSize() {
		return 24;
	}
	
	public boolean isAble(DateTime date){
		return condition[date.getHourOfDay()];
	}
	
}