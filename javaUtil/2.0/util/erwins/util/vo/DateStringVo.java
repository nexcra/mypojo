package erwins.util.vo;

import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;

import erwins.util.lib.JodaUtil;

/**
 * Date를 문자열로 변경하여 mybatis sql의 between로직을 만들때 사용한다.
 * @author sin
 */
public class DateStringVo{
    

    
    /** 마지막일로부터 X일까지의 between을 구한다.
     * ex) 오늘포함 7일간 이라면 DateStringVo.betweenByEndDate(JodaUtil.YMDHMSS, new DateMidnight(), 7);  */
    public static DateStringVo betweenByEndDate(DateTimeFormatter format,DateMidnight endDate,int interval){
        DateStringVo instance = new DateStringVo();
        instance.startDate = format.print(endDate.minusDays(interval-1));
        instance.endDate = format.print(JodaUtil.endTimeOfDay(endDate ));
        return instance;
    }
    
    
    
    private String startDate;
    private String endDate;
    
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    
    
    
}
