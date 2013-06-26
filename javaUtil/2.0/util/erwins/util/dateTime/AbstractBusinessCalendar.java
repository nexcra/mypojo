package erwins.util.dateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

/**
 * 영업일(공휴일/토,일요일을 제외한 일자) 계산기. 
 * 오픈소스에 버그가 있어서 걍 만듬
 * 늦은로딩을 하도록 싱들톤으로 제작할것. 
 * ex) public static synchronized BusinessCalendar instance() 
 * 추후 JodaTime으로 만들도록 하자
 * @author sin
 */
public abstract class AbstractBusinessCalendar {

    /** 이 로직은 시분초를 무시함으로 key를 Date로 하지 않는다 */
    private Map<String, String> holidayMap = new HashMap<String, String>();

    private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    
    /** 휴일추가 */
    public void addHoliday(String date,String desc) {
        holidayMap.put(date, desc);
    }
    
    /** 양력 일자 고정인 기본 휴일들 추가 */
    public void addDefaultHoliday(String year){
        addHoliday(year+"0101","신정");
        addHoliday(year+"0301","삼일절");
        addHoliday(year+"0505","어린이날");
        addHoliday(year+"0606","현충일");
        addHoliday(year+"0815","광복절");
        addHoliday(year+"1003","개천절");
        addHoliday(year+"1225","크리스마스");
    }
    
    /**
     * 해당 일자가 휴일인지?
     */
    public boolean isHoliday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
        case Calendar.SATURDAY:
            return true;
        default:
            String dateString = dateFormat.format(date);
            return holidayMap.containsKey(dateString);
        }
    }

    /** 오늘(basicDate)은 해당되지 않는다.
     * 4를 입력하면 basicDate늘 제외한 4번째 영업일이 리턴된다.  */
    public Date getBusinessdayDate(Date basicDate,int amount) {
        Date tempDate = basicDate;
        if (amount > 0) { // 기준일 이후
            while (amount > 0) {
                tempDate = DateUtils.addDays(tempDate, 1);
                if (!isHoliday(tempDate))  amount--;
            }
        } else if (amount < 0) { // 기준일 이전
            while (amount < 0) {
                tempDate = DateUtils.addDays(tempDate, -1);
                if (!isHoliday(tempDate))  amount++;
            }
        }else throw new RuntimeException("잘못된 인수 : " + amount);
        return tempDate;
    }
    
    public Date getBusinessdayDateFromToday(int amount) {
        return getBusinessdayDate(new Date(),amount);
    }

}
