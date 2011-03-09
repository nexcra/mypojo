
package erwins.util.webapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import erwins.util.root.EntityHibernatePaging;
import erwins.util.tools.StringBuilder2;


public abstract class AppUtil{

	/** 직접 커넥션을 줄 수 없어서 이렇게 REST자료를 얻어와야 한다. */
	public static String rest(String urlString){
		try {
			URL url = new URL(urlString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder2 b = new StringBuilder2(); 
			String line;
			while((line=reader.readLine())!=null){
				b.appendLine(line);
			}
			reader.close();
			return b.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static final String NORMAL = "yyyy년MM월dd일-HH시mm분";
	
	public static String  dateString(){
		return dateString(NORMAL);
	}
	
	/** Enum인 DayUtil을 사용하면 초기회 오류난다.. ㅅㅂ */
	public static String  dateString(String format){
		Calendar c = Calendar.getInstance(Locale.KOREA);
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.KOREA);
        c.add(Calendar.HOUR, 9); //이상하게 9시간 느리다.. ㅅㅂ 멀 잘못한거양.
        return f.format(c.getTime());
	}
	
	/** 전체페이지를 구하지 못함으로 걍 갯수만 표시해 주자. */
	public static <T extends EntityHibernatePaging> void  rownum(Collection<T> result){
		int num  = result.size();
		for(T each : result) each.setRownum(num--);
	}
	
}
