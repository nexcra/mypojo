
package erwins.util.dateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

import org.joda.time.DateTime;

/**
 * 시분초를 문자열로 바꿔준다.
 */
public class TimeString {

	/** 밀리초 */
    long millis;
    long totalSecond;
    int h;
    int MM;
    int ss;

    public TimeString() {
    	millis = System.currentTimeMillis();
    }
    public TimeString(long millis) {
    	this.millis = millis;
    	initTime();
    }
    
    public static TimeString between(DateTime from ,DateTime to){
    	TimeString ts = new TimeString(to.getMillis() - from.getMillis());
    	return ts;
    }
    
    
    public TimeString(int[] time) {
    	if(time==null || time.length > 3) throw new IllegalArgumentException();
    	if(time.length > 0) h = time[0];
    	if(time.length > 1) MM = time[1];
    	if(time.length > 2) ss = time[2];
    }

    private void initTime() {
    	totalSecond = millis / 1000;
    	h = (int) (totalSecond / 60 / 60);
    	MM = (int) ((totalSecond - (h*60*60)) / 60);
    	ss = (int) (totalSecond % 60);
    }
    
    /** 시간차이를 구할때 사용한다. */
    public TimeString stop() {
    	long start = millis;
    	long stop = System.currentTimeMillis(); 
    	millis = stop - start;
    	initTime();
        return this;
    }

    public boolean isLarge(int second) {
        return totalSecond > second ? true : false;
    }

    /** 예쁘게 보기~ 입력하는 second는 최소 초. */
    public String toString(int second) {
        if (isLarge(second)) return MessageFormat.format("{0}:{1}:{2}", h, MM, ss);
        return MessageFormat.format("{0}ms", millis); //milli        
    }
    
    /** 시분초를 나누어 문자열을 제작한다. 24시간이 넘을 경우 적절히 조절한다. */
    @Override
    public String toString(){
    	if(h==0 && MM==0 && ss==0){
    		stop();
    	}
    	if(h > 24){
    		int hour = h%24;
    		int day = h/24;
    		if(day > 365){
    			int year = day / 365; 
    			day = day % 365;
    			return MessageFormat.format("{0}년 {1}일 {2}시간",  year,day,hour);
    		}
    		return MessageFormat.format("{0}일 {1}시간 {2}분",  day,hour, MM);
    	}
		if(h!=0) return MessageFormat.format("{0}시간 {1}분 {2}초",  h, MM, ss);
        else if(MM!=0) return MessageFormat.format("{0}분 {1}초", MM,ss);
        else{
        	if(ss>10) return MessageFormat.format("{0}초", ss);
        	else {
        		BigDecimal c = new BigDecimal((double)millis / 1000).setScale(2,RoundingMode.HALF_UP);
        		return c.toString() + "초";
        	}
        }
    }

}