package erwins.util.lib;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import erwins.util.valueObject.ShowTime;

/**
 * 년도, 일자 등의 처리. singleton
 * @author     erwins(my.pojo@gmail.com)
 */
public enum Days{
    
    /** yyyy/MM/dd */
    DATE_SIMPLE(new SimpleDateFormat("yyyy/MM/dd")),

    /** yyyy년MM월dd일HH시mm분 */
    DATE(new SimpleDateFormat("yyyy년MM월dd일-HH시mm분")),
    
    /** yyyy.MM.dd_HH.mm.ss */
    DATE_FILE(new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss")),
    
    /** yyyyMMdd */
    DATE_FOR_DB(new SimpleDateFormat("yyyyMMdd")),

    /** yyyy */
    YEAR(new SimpleDateFormat("yyyy")),
    
    /** yyyy */
    YY(new SimpleDateFormat("yy")),
    
    YY_MONTH(new SimpleDateFormat("yyMM")),

    /** MM */
    MONTH(new SimpleDateFormat("MM")),
    
    MONTH_DAY(new SimpleDateFormat("MMdd")),
    
    /** dd */
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
        if(value==null) return "";
        return format.format(value.getTime());
    }
    
    /** Calendar를 SimpleDateFormat으로 변환하여 반환 */
    public String get(Date value) {
        if(value==null) return "";
        return format.format(value.getTime());
    }    

    /** 현재 일자를 반환 */
    public String get() {
        return format.format(Calendar.getInstance().getTime());
    }
    /** 현재 일자를 int로 반환. 사용시 주의! */
    public int getIntValue() {
        return Integer.parseInt(get());
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
     * 현재 시각에서 일/월/년을 더한다. 리턴값은 해당 String 문자열이다.
     */ 
    public String add(Integer ... time) {
        Calendar c =  addCalendar(time);
        return this.get(c);
    }
    
    
    // ===========================================================================================
    //                                  static  
    // ===========================================================================================

    /**
     * 현재 시각에서 일/월/년을 더한다. 더해지는 순서는 년도부터(역순)이다.
     * @param 일/월/년 
     * ex) add(1,0,3) => 3년 1일 
     */ 
    public static Calendar addCalendar(Integer ... time) {
        return addCalendar(Calendar.getInstance(),time);
    }
    
    /**
     * Calendar 시각에서 일/월/년을 더한다. 더해지는 순서는 년도부터(역순)이다.
     * @param 일/월/년 
     * ex) add(1,0,3) => 3년 1일 
     */ 
    public static Calendar addCalendar(Calendar c,Integer ... time) {
        if(time.length > 3) throw new RuntimeException(time.length + " too large size");
        if(time.length >= 3) c.add(Calendar.YEAR, time[2]);
        if(time.length >= 2) c.add(Calendar.MONTH, time[1]);
        if(time.length >= 1) c.add(Calendar.DATE, time[0]);
        return c;
    }
    
    /**
     * Calendar 시각에서  시분초를 더한다.
     * @param 일/월/년 
     * ex) add(1,0,3) => 3년 1일 
     */ 
    public static Calendar addCalendarTime(Calendar c,Integer ... time) {
    	if(time.length > 3) throw new RuntimeException(time.length + " too large size");
    	if(time.length >= 3) c.add(Calendar.HOUR, time[2]);
    	if(time.length >= 2) c.add(Calendar.MINUTE, time[1]);
    	if(time.length >= 1) c.add(Calendar.SECOND, time[0]);
    	return c;
    }
    
    public static Calendar addCalendar(Date d,Integer ... time) {
    	return addCalendar(getCalendar(d),time);
    }
    
    /**
     * 4,2,2자리 년월일을 받아서 Carender 객체를 리턴한다.
     * 시간은 0시0분0초로 초기화 한다. 
     */
    public static Calendar getCalendar(int year, int month, int day) {
    	return getCalendar(year,month,day,0,0);
    }
    
    /**
     * 4,2,2자리 년월일을 받아서 Carender 객체를 리턴한다.
     */
    public static Calendar getCalendar(int year, int month, int day,int h,int m) {
    	Calendar cal = Calendar.getInstance();
    	cal.setLenient(false);
    	cal.set(year,month-1,day,h,m,0);
    	return cal;
    }
    
    /** Date로 Calendar를 생성한다. */
    public static Calendar getCalendar(Date date) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
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
    
    public static int betweenDate(Calendar cal1, Calendar cal2) {
    	return betweenDate(cal1.getTime(),cal2.getTime());
    }

    /**
     * 두 객체간의 일자를 리턴한다.
     * @param1,2 Calendar객체
     */
    public static int betweenDate(Date cal1, Date cal2) {
    	long diff = cal2.getTime() - cal1.getTime();
    	return (int)(diff/24/60/60/1000);
    }

    /**
     * 두 객체간의 일자를 리턴한다.
     * @param1,2 String 8자리 ex) "20080216"
     */	
    public static int betweenDate(String strDate1, String strDate2) {
    	return betweenDate(getCalendar(strDate2).getTime(), getCalendar(strDate1).getTime());
    }
    
    /**
     * 두 객체간의 사간차를 String 시분초로 리턴한다.
     */
    public static String betweenTimeStr(Date cal1, Date cal2) {
        return new ShowTime(betweenTime(cal1,cal2)).toString();
    }
    
    /**
     * 두 객체간의 사간차를 String 시분초로 리턴한다.
     */
    public static int[] betweenTime(Date start, Date end) {
        long diffSecond = (end.getTime() - start.getTime())/1000;
        int h = (int)diffSecond / 60/60;
        diffSecond = diffSecond - h * 60 * 60;
        int mm = (int)diffSecond / 60;
        int ss = (int)diffSecond % 60;
        return new int[]{h,mm,ss};
    }
    
    public static final String[] WEEK_KOREAN = {"일","월","화","수","목","금","토"}; 
    
    /** 요일을 리턴한다. */
    public static String getDayOfWeek(Calendar d){
    	return WEEK_KOREAN[d.get(Calendar.DAY_OF_WEEK)-1];
    }
    
    /** 요일을 리턴한다. 나중에 패턴으로 바꿀것. */
    public static String getDayStr(Calendar c){
        return String.format("%tB %<tC일 %<tA", c);
    }
    /** 요일을 리턴한다. 나중에 패턴으로 바꿀것. */
    public static String getDayStr(){
    	return getDayStr(Calendar.getInstance());
    }
    

}