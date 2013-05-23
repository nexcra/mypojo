package erwins.util.vo;

import java.util.Date;

import org.joda.time.DateMidnight;

/**
 * Db의 년월일 조회 파라메터로 쓰이는 vo  
 * mybatis 등에서 사용하자
 * 향후 확장하자.
 * @author sin
 */
public class DateTimeVo{
    
    private Date startDate;
    private Date endDate;
    
    /** 오늘 일자를 구한다.  */
    public static DateTimeVo betweenByNow(){
        return betweenByMidnight(new DateMidnight());
    }
    
    public static DateTimeVo betweenByMidnight(DateMidnight midnight){
    	DateTimeVo vo = new DateTimeVo();
    	vo.startDate = midnight.toDate();
    	vo.endDate = midnight.toDateTime().plusDays(1).minusMillis(1).toDate();
        return vo;
    }

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
    
    
    
}
