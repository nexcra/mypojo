package erwins.util.dateTime;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

/**
 * Db의 년월일 조회 파라메터로 쓰이는 vo
 * 년월일 문자열을 DATE로 변경해준다
 * mybatis 등에서 사용하자 (타입핸들러 추가)
 * @author sin
 */
public class DateTimeVo{
    
    private DateTime start;
    private DateTime end;
    private DateMidnight date;
    
    public static DateTimeVo between() {
    	return DateTimeVo.between(new DateMidnight());
    }
    
	/** 둘다 동일 일자를 세팅한다.  종료일은 1일 더한 후 1밀리초를 빼준다. */
	public static DateTimeVo between(String date) {
		DateTimeVo vo = new DateTimeVo();
		vo.start = JodaUtil.toDateMidnight(date).toDateTime();
		vo.end =  vo.start.plusDays(1).minusMillis(1);
		return vo;
	}
	
	/** 둘다 동일 일자를 세팅한다. */
	public static DateTimeVo between(DateMidnight date) {
		DateTimeVo vo = new DateTimeVo();
		vo.start = date.toDateTime();
		vo.end = vo.start.plusDays(1).minusMillis(1);
		return vo;
	}
	

	public DateTime getStart() {
		return start;
	}

	public void setStart(DateTime start) {
		this.start = start;
	}

	public DateTime getEnd() {
		return end;
	}

	public void setEnd(DateTime end) {
		this.end = end;
	}

	public DateMidnight getDate() {
		return date;
	}

	public void setDate(DateMidnight date) {
		this.date = date;
	}

    
}
