package erwins.util.lib;

import java.text.*;
import java.util.*;

import javax.servlet.http.HttpSession;

/**
 * 년도, 일자 등의 처리. singleton
 * @author     erwins(my.pojo@gmail.com)
 */
public enum Days{
    
    /**
     * yyyy/MM/dd
     */
    DATE_SIMPLE(new SimpleDateFormat("yyyy/MM/dd")),

    /**
     * yyyy년MM월dd일HH시mm분
     */
    DATE(new SimpleDateFormat("yyyy년MM월dd일HH시mm분")),
    
    /**
     * yyyy.MM.dd_HH.mm.ss
     */
    DATE_FILE(new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss")),
    

    /**
     * yyyyMMdd
     */
    DATE_FOR_DB(new SimpleDateFormat("yyyyMMdd")),

    /**
     * yyyy
     */
    YEAR(new SimpleDateFormat("yyyy")),

    /**
     * MM
     */
    MONTH(new SimpleDateFormat("MM")),
    
    /**
     * dd
     */
    DAY(new SimpleDateFormat("dd"));
    
    private Format format;

    private Days(Format format) {
        this.format = format;
    }
    
 // 한국 표준시 설정
    static {
        Locale.setDefault(Locale.KOREA); // 로캘 고정 
        TimeZone.setDefault(new SimpleTimeZone(9 * 60 * 60 * 1000, "KST")); //한국시를 +9시간 해서 표준으로 잡아준다.
    };
    
    /** Calendar를 SimpleDateFormat으로 변환하여 반환 */
    public String get(Calendar value) {
        return format.format(value.getTime());
    }
    
    /** Calendar를 SimpleDateFormat으로 변환하여 반환 */
    public String get(Date value) {
        return format.format(value.getTime());
    }    

    /** 현재 일자를 반환 */
    public String get() {
        return format.format(Calendar.getInstance().getTime());
    }
    
    /**
     * 8자리 년월일을 받아서 일/월/년을 더한다. 더해지는 순서는 년도부터(역순)이다.
     * @param 일/월/년 
     * ex) add(1,0,3) => 3년 1일 
     */ 
    public String add(String yyyyMMdd,Integer ... time) {
        if(time.length > 3) return null;
        Calendar c =  getCalendar(yyyyMMdd);
        if(time.length >= 3) c.add(Calendar.YEAR, time[2]);
        if(time.length >= 2) c.add(Calendar.MONTH, time[1]);
        if(time.length >= 1) c.add(Calendar.DATE, time[0]);
        return this.get(c);
    }
    
    /**
     * 현재 시각에서 일/월/년을 더한다. 더해지는 순서는 년도부터(역순)이다.
     * @param 일/월/년 
     * ex) add(1,0,3) => 3년 1일 
     */ 
    public String add(Integer ... time) {
        if(time.length > 3) return null;
        Calendar c =  Calendar.getInstance();
        if(time.length >= 3) c.add(Calendar.YEAR, time[2]);
        if(time.length >= 2) c.add(Calendar.MONTH, time[1]);
        if(time.length >= 1) c.add(Calendar.DATE, time[0]);
        return this.get(c);
    }
    
    // ===========================================================================================
    //                                  static  
    // ===========================================================================================

    /**
     * 4,2,2자리 년월일을 받아서 Carender 객체를 리턴한다. 
     */
    public static Calendar getCalendar(int year, int month, int day) {
    	Calendar cal = null;
    	try {
    		cal = Calendar.getInstance();
    		cal.setLenient(false);
    		cal.set(year,month-1,day);
    		cal.getTime();
    	} catch(Exception e) {}
    	return cal;
    }
    
    /**
     * 즉석 패턴을 만들어서 출력한다. 
     */
    public static String formatDate(String pattern) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat f;
        f = new SimpleDateFormat(pattern, Locale.KOREA);
        return f.format(c.getTime());
    }

    /**
     * 8자리 년월일을 받아서 Carender 객체를 리턴한다. 
     */	
    public static Calendar getCalendar(String str) {
    	if(str.length() != 8) return null;
    	int year = Integer.parseInt(str.substring(0,4));
    	int month = Integer.parseInt(str.substring(4,6));
    	int day = Integer.parseInt(str.substring(6,8));
    	return getCalendar(year,month,day);
    }
    
    
    public static String sin(Calendar cal1, Calendar cal2) {
        double day = betweenDate(Calendar.getInstance(),getCalendar("20070601") );
        day += 741;
        return Formats.DOUBLE1.get(day / 365);
    }
    

    /**
     * 두 객체간의 일자를 리턴한다.
     * @param1,2 Calendar객체
     */
    public static int betweenDate(Calendar cal1, Calendar cal2) {
    	long diff = cal2.getTime().getTime() - cal1.getTime().getTime();
    	return (int)(diff/24/60/60/1000);
    }

    /**
     * 두 객체간의 일자를 리턴한다.
     * @param1,2 String 8자리 ex) "20080216"
     */	
    public static int betweenDate(String strDate1, String strDate2) {
    	return betweenDate(getCalendar(strDate2), getCalendar(strDate1));
    }
    
    /**
     * 두 객체간의 사간차를 String 시분초로 리턴한다.
     */
    public static String betweenTimeStr(Calendar cal1, Calendar cal2) {
        return toString(betweenTime(cal1,cal2));
    }
    
    /**
     * 두 객체간의 사간차를 String 시분초로 리턴한다.
     */
    public static long[] betweenTime(Calendar cal1, Calendar cal2) {
        long diffSecond = (cal2.getTime().getTime() - cal1.getTime().getTime())/1000;
        long h = diffSecond / 60/60;
        long mm = diffSecond / 60;
        long ss = diffSecond % 60;
        return new long[]{h,mm,ss};
    }
    
    /**
     * 세션의 나이를 String으로 리턴한다.
     * minit 보다 많은 나이는 null을 리턴한다.
     * minit이 null이면 제약 없음 
     */
    public static String getAgeStr(HttpSession session) {
        return toString(getAge(session));
    }
    
    /**
     * 세션의 나이를 리턴한다.
     * minit 보다 많은 나이는 null을 리턴한다.
     * minit이 null이면 제약 없음 
     */
    public static long[] getAge(HttpSession session) {
        Calendar lastAccessTime = Calendar.getInstance();
        lastAccessTime.setTimeInMillis(session.getLastAccessedTime());
        return betweenTime(lastAccessTime, Calendar.getInstance()); 
    }
    
    /** 시분초를 나누어 문자열을 제작한다. */
    private static String toString(long ... time){
        if(time[0]!=0) return MessageFormat.format("{0}시 {1}분 {2}초", time[0],time[1],time[2]);
        else if(time[1]!=0) return MessageFormat.format("{0}분 {1}초", time[1],time[2]);
        else return MessageFormat.format("{0}초", time[2]);
    }
    
    /** 요일을 리턴한다. 나중에 패턴으로 바꿀것. */
    public static String getDayStr(){
        return String.format("%tB %<tC %<tA", Calendar.getInstance());
    }
    
    

}