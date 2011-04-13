
package erwins.test.runAndSeeResult;

import java.util.Calendar;

import org.junit.Test;

import erwins.util.lib.DayUtil;
import erwins.util.lib.FormatUtil;

public class Career {

    /** 시밤 경력.. ㅠㅠ */
    @Test
    public void career() {
        
        Calendar now = Calendar.getInstance();
        
        int army = (int) (741 * 0.8 * 0.5) ;
        System.out.println("군경력인정일 : " + army);
        /** 기사취득일 */
        Calendar gisa = DayUtil.getCalendar("20081215");
        int before = (int) (DayUtil.betweenDate(DayUtil.getCalendar("20070601"),gisa) * 0.5);
        System.out.println("기사이전 경력인정일 : "+ before );
        int after = DayUtil.betweenDate(gisa,now );
        System.out.println("기사이후 경력인정일 : "+ after );
        
        int sum = army + before + after;
        System.out.println("협회 인정 합산경력일 : "+ sum );
        System.out.println("협회 인정 경력 : "+FormatUtil.DOUBLE2.get(sum / 365.0) + "년");
        
        int sum2 = 741 + before*2 + after;
        System.out.println("협회 비인정 경력 : "+FormatUtil.DOUBLE2.get(sum2 / 365.0) + "년");
        
        int needDay = 365 * 7 - sum;
        
        System.out.println("고급 되는일자 : "+DayUtil.DATE_SIMPLE.add(needDay));
        
    }
}
