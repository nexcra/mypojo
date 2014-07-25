
package erwins.util.webapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


public abstract class AppUtil{
	
	/** UTF-8을 기본 */
	public static String getUrlText(String urlString){
		return getUrlText(urlString,"UTF-8");
	}

	/** 직접 커넥션을 줄 수 없어서 이렇게 REST자료를 얻어와야 한다. */
	public static String getUrlText(String urlString,String encode){
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream(),encode));
			return IOUtils.toString(reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			IOUtils.closeQuietly(reader);
		}
	}
	
	/** 흠.. 로케일 그냥두면 에러나길래 일단 일케 땜빵.. ㅠㅠ */
    public static Date toLocaleKorea(Date date) {
    	Calendar calendar = Calendar.getInstance(Locale.KOREA);
    	calendar.setTime(date);
    	calendar.add(Calendar.HOUR, 9);
    	return calendar.getTime();
    }
	
	
	private static final String NORMAL = "yyyy년MM월dd일-HH시mm분";
	
	public static String  dateString(){
		return dateString(NORMAL);
	}
	
	/** Enum인 DayUtil을 사용하면 초기회 오류난다.. ㅅㅂ */
	@Deprecated
	public static String  dateString(String format){
		Calendar c = Calendar.getInstance(Locale.KOREA);
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.KOREA);
        c.add(Calendar.HOUR, 9); //이상하게 9시간 느리다.. ㅅㅂ 멀 잘못한거양.
        return f.format(c.getTime());
	}
	
	/** 전체페이지를 구하지 못함으로 걍 갯수만 표시해 주자. */
/*	public static <T extends EntityHibernatePaging> void  rownum(Collection<T> result){
		int num  = result.size();
		for(T each : result) each.setRownum(num--);
	}*/
	
	public static void addQueue(String url){
		Queue queue = QueueFactory.getDefaultQueue();
	    queue.add(TaskOptions.Builder.withUrl(url));
	}
	
}
