
package erwins.util.dateTime;

import java.util.regex.Pattern;

import erwins.util.text.RegEx;

/** X자리 숫자 1,0으로 true / false를 나타낸다. */
public abstract class AbstractStringBooleanCondition{
	
	protected abstract int getSize();
	
	protected final boolean[] condition = new boolean[getSize()];
	
	public AbstractStringBooleanCondition(String dayStr){
		if(dayStr.length() != getSize()) throw new IllegalArgumentException(getSize()+"자리 1,0만 허용됩니다." + dayStr);
		if(!RegEx.isFullMatch(Pattern.compile("[01]*"), dayStr)) throw new IllegalArgumentException(getSize()+"자리 1,0만 허용됩니다." + dayStr);
		for(int i=0;i<getSize();i++){
			condition[i] = dayStr.substring(i,i+1).equals("1");
		}
	}
	public boolean isAble(int dow){
		return condition[dow];
	}

}