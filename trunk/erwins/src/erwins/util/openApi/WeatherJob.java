
package erwins.util.openApi;

import org.apache.ecs.html.Span;
import org.springframework.stereotype.Component;

import erwins.util.lib.Strings;
import erwins.util.vender.quartz.JobRunable;

/**
 * 날시를 주기적으로 체크하는 job이다.
 * @author erwins(quantum.object@gmail.com)
 **/
@Component
public class WeatherJob implements JobRunable{
    
    private static String[] weathers; //5개 3일 후 까지

    public void jobRun() {
        run();
    }
    
    public static void run(){
        weathers = Google.getWeatherStr("Inchon");
    }
    
    /** 
     * 간단한 String문자열로 날씨를 경고한다. 
     */
    public static String alertWeather(){
        if(weathers==null) run();
        String weather = "맑음";
        for(int i=0;i<weathers.length;i++){
            if(Strings.isMatch(weathers[i],"비")){
                switch(i){
                    case 0 : weather = "지금"; break;
                    case 1 : weather = "오늘"; break;
                    case 2 : weather = "내일"; break;
                    case 3 : weather = "2일후"; break;
                    case 4 : weather = "3일후"; break;
                }
                weather += " " + weathers[i];
                Span span = new Span(weather);
                span.setStyle("color:red;font-weight: bold;");
                return span.toString() ;
            }
        }
        return weather;
    }

}
