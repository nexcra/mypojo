package erwins.webapp.myApp.admin
import static org.junit.Assert.*

import java.util.Calendar

import erwins.util.lib.DayUtil
import erwins.util.lib.FormatUtil

abstract class  Career {

	public static String career(){
		def 경력 = []
		Calendar now = Calendar.getInstance();
		int army = 741 * 0.8 * 0.5 ;
		경력 << "군경력인정일 : ${army}일"
		/** 기사취득일 */
		Calendar gisa = DayUtil.getCalendar("20081215");
		int before = (int) (DayUtil.betweenDate(DayUtil.getCalendar("20070601"),gisa) * 0.5);
		경력 << "기사이전 경력인정일  : ${before}일"
		int after = DayUtil.betweenDate(gisa,now );
		경력 << "기사이후 경력인정일  : ${after}일"
		
		int sum2 = 741 + before*2 + after;
		경력 << "전체 경력 : ${FormatUtil.DOUBLE2.get(sum2 / 365.0)}년"
		int sum = army + before + after;
		경력 << "협회 인정(기사자격증) 경력 : ${FormatUtil.DOUBLE2.get(sum / 365.0)}년"
		
		int needDay = 365 * 7 - sum;
		경력 << "고급 되는일자           : ${DayUtil.DATE_SIMPLE.add(needDay)}"
		return 경력.join('<br>')
	}
}
