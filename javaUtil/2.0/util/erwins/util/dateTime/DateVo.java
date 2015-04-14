package erwins.util.dateTime;

import java.util.Date;

import lombok.Data;

import org.joda.time.LocalDate;
import org.junit.Test;

import erwins.util.dateTime.JodaUtil.Joda;

@Data 
public class DateVo{
    
    private Date start;
    private Date end;
    
    /** 어제포함 interval일간  (오늘 미포함) */
    public static DateVo fromYesterday(int interval) {
    	return from(new LocalDate(),interval);
    }
	
	/** 
	 * 기준일로부터 X일 이후부터 기준일까지
	 * basicDate가 오늘이면 어제저녁 00시 가 end
	 * interval이 1이면 1일간의 자료 비교
	 *  */
	public static DateVo from(LocalDate basicDate,int interval) {
		DateVo vo = new DateVo();
		vo.end = basicDate.toDate();
		vo.start = basicDate.minusDays(1).toDate();
		return vo;
	}
	
	@Test
	public void test(){
		DateTimeVo a = DateTimeVo.fromYesterday(2);
		System.out.println(Joda.TIME_KR.get(a.getStart()));
		System.out.println(Joda.TIME_KR.get(a.getEnd()));
	}

    
}
