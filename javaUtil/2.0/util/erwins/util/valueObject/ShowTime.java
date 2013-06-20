
package erwins.util.valueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;

/**
 * 시분초를 나타내는 범용 TimeClass
 * 여서 ms는 마이크로초가 아닌 밀리초(1/1000s)이다. 
 */
public class ShowTime {

    /**
     * @uml.property  name="totalNanoSecond"
     */
    long totalNanoSecond;
    long totalSecond;
    int h;
    int MM;
    int ss;

    public ShowTime() {
    	
    }
    public ShowTime(long nanoTime) {
        totalNanoSecond = nanoTime;
        initNanoTime();
    }
    
    public ShowTime(int[] time) {
    	if(time==null || time.length > 3) throw new IllegalArgumentException();
    	if(time.length > 0) h = time[0];
    	if(time.length > 1) MM = time[1];
    	if(time.length > 2) ss = time[2];
    }

    private void initNanoTime() {
        totalSecond = totalNanoSecond / 1000 / 1000 / 1000;
        initTime();
    }
    public ShowTime initTime(long totalSecond) {
    	this.totalSecond = totalSecond;
    	initTime();
    	return this;
    }
    public ShowTime setMiliSec(long ms) {
    	totalNanoSecond = ms * 1000 * 1000;
        initTime();
    	return this;
    }
    private void initTime() {
    	h = (int) (totalSecond / 60 / 60);
    	MM = (int) ((totalSecond - (h*60*60)) / 60);
    	ss = (int) (totalSecond % 60);
    }

    public boolean isLarge(int second) {
        return totalSecond > second ? true : false;
    }

    public boolean isLarge(long nanoSecond) {
        return totalNanoSecond > nanoSecond ? true : false;
    }

    /** 예쁘게 보기~ 입력하는 second는 최소 초. */
    public String toString(int second) {
        if (isLarge(second)) return MessageFormat.format("{0}:{1}:{2}", h, MM, ss);
        else if (isLarge(1000000L)) return MessageFormat.format("{0}ms", totalNanoSecond / 1000 / 1000); //milli        
        else return MessageFormat.format("{0}ns", totalNanoSecond);
    }
    
    /** 시분초를 나누어 문자열을 제작한다. 24시간이 넘을 경우 적절히 조절한다. */
    @Override
    public String toString(){
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
        		BigDecimal c = new BigDecimal((double)totalNanoSecond / 1000 / 1000 / 1000).setScale(2,RoundingMode.HALF_UP);
        		return c.toString() + "초";
        	}
        }
    }

    /**
     * @return
     * @uml.property  name="totalNanoSecond"
     */
    public long getTotalNanoSecond() {
        return totalNanoSecond;
    }

}