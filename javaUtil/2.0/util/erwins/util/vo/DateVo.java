package erwins.util.vo;

import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import erwins.util.lib.JodaUtil;

/**
 * Db의 년월일 조회 파라메터로 쓰이는 vo
 * 년월일 문자열을 DATE로 변경해준다
 * mybatis 등에서 사용하자 ( value 이외에는 get,set 프리픽스 금지.)
 * @author sin
 */
public class DateVo{
    
    private DateTime start;
    private DateTime end;
    
    public DateVo start(DateTime start) {
		this.start = start.toDateTime();
		return this;
	}

	public DateVo end(DateTime end) {
		this.end = end;
		return this;
	}
	
    public DateVo start(String start) {
		this.start = JodaUtil.toDateMidnight(start).toDateTime();
		return this;
	}

	public DateVo end(String end) {
		this.end = JodaUtil.toDateMidnight(end).toDateTime();
		return this;
	}
	
	/** 둘다 동일 일자를 세팅한다. */
	public DateVo date(String date) {
		this.start = JodaUtil.toDateMidnight(date).toDateTime();
		this.end = start;
		return parse();
	}
	/** 둘다 동일 일자를 세팅한다. */
	public DateVo date(DateMidnight date) {
		this.start = date.toDateTime();
		this.end = start;
		return parse();
	}
	
	/** 종료일은 1일 더한 후 1밀리초를 빼준다. */
	public DateVo parse() {
		startDate = start.toDate();
		endDate = end.plusDays(1).minusMillis(1).toDate();
		return this;
	}
	
    //=================== getter / settter ==============================

    private Date startDate;
    private Date endDate;
    
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
    
}
