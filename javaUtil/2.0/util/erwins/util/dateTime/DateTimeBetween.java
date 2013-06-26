package erwins.util.dateTime;

import org.joda.time.ReadableInstant;

/** 
 * 시작일 종료일(특히 년월일 문자열)을 DB에 넣어서 사용하는로직 등에 사용
 * 간단한 일자비교. */
public class DateTimeBetween {

	public final ReadableInstant start;
    public final ReadableInstant end;
    
    public boolean isBetween(ReadableInstant value){
        if(value.isBefore(start)) return false;
        if(value.isAfter(end)) return false;
        return true;
    }
    
    /** 한쪽 바운더리라도 겹치는지?
     * 각 바운더리의 경계만 체크한다 */
    public boolean isBetweenAny(DateTimeBetween value){
        if(value.end.isBefore(start)) return false;
        if(end.isBefore(value.start)) return false;
        return true;
    }
    
    public DateTimeBetween(ReadableInstant start,ReadableInstant end){
        this.start = start;
        this.end = end;
        if(start.isAfter(end)) throw new IllegalArgumentException("Illegal datetime : "+start + end);
    }
    
    /*
    @Test
    public void test() throws InterruptedException{
        
        DateTime a = JodaUtil.YMD.parseDateTime("20130101");
        DateTime b = JodaUtil.YMD.parseDateTime("20130119");
        
        JodaBwtween jb1 = new JodaBwtween(JodaUtil.YMD.parseDateTime("20130101"),JodaUtil.YMD.parseDateTime("20130110"));
        JodaBwtween jb2 = new JodaBwtween(JodaUtil.YMD.parseDateTime("20110110"),JodaUtil.YMD.parseDateTime("20150119"));
        
        System.out.println(jb1.isBetweenAny(jb2));
        System.out.println(jb2.isBetweenAny(jb1));
        
        System.out.println(Days.daysBetween(a, b).getDays());
        System.out.println(Days.daysBetween(b, a).getDays());
    }*/
	
}
