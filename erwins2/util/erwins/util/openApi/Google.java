
package erwins.util.openApi;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.ecs.html.Script;
import org.apache.ecs.wml.Img;

import erwins.domain.SystemInfo;
import erwins.util.lib.CharEncodeUtil;
import erwins.util.lib.StringUtil;
import erwins.util.vender.apache.RESTful;


/**
 * GoogleWeather를 래핑한다.
 * @author erwins(my.pojo@gmail.com)
 */
public class Google{
    
    private static final String GOOGLE_MAP_URL = "http://maps.google.com/maps";
    private static final String GOOGLE_CHART_URL = "http://chart.apis.google.com/chart";
    private static final String GOOGLE_WEATHER_URL = "http://www.google.co.kr/ig/api";
    private static final String GOOGLE_LOCAL_MAP_KEY = "ABQIAAAAbnojiUetDB2JPnAr7msMxhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSSUTahXG1TgB4AL-LL3DXVwdGvXg";
    private static final String GOOGLE_SERVER_MAP_KEY = "ABQIAAAAbnojiUetDB2JPnAr7msMxhQ0X3PD8M9p51GbyLnt4zg9AYh1ZBSqd86Pt0zb2S1bkxjecXYnIKuHhw";
    
    /**
     * 지정된 서버IP가 아니면 LOCAL Key를 , 지정된 서버이면 서버 key를 리턴한다.
     */
    public static String getGoogleMapKey(){
        return SystemInfo.isServer() ? GOOGLE_SERVER_MAP_KEY : GOOGLE_LOCAL_MAP_KEY;
    }

    /**
     * 주소 변경시 일괄 적용 위함 
     */
    public static String getGoogleScript(){
        Script js = new Script();
        js.setSrc(GOOGLE_MAP_URL+"?file=api&amp;v=2&amp;key="+getGoogleMapKey());
        js.setType("text/JavaScript");
        return js.toString();
    }
    
    // ===========================================================================================
    //                                   API
    // ===========================================================================================    
    
    private static enum ChartMode{
        p3;
        public String get(){
            return "cht=" + this.toString();
        }
    }
    
    /**
     * 구글 차트 정보를 img태그로 리턴한다.
     */
    public static String getChart(Map<String,Object> map,int width,int height){
        String size = MessageFormat.format("chs={0}x{1}", width,height);
        List<String> label = new ArrayList<String>();
        List<String> value = new ArrayList<String>();
        
        for(Entry<String,Object> entry : map.entrySet()){
            label.add(entry.getKey().toString() +"("+ entry.getValue().toString()+")");
            value.add(entry.getValue().toString());
        }
        String a = "chl=" + StringUtil.joinTemp(label,"|");
        String b = "chd=t:" + StringUtil.joinTemp(value,",");
        
        Img img = new Img(GOOGLE_CHART_URL+"?"+size+"&"+a+"&"+b+"&"+ChartMode.p3.get());
        return img.toString();
    }
    
    /** 구글 날씨를 XML로 리턴한다. */
    public static String getWeatherXml(String city){
        String cityName = StringUtil.nvl(city,"seoul");
        NameValuePair[] parmas = new NameValuePair[]{new NameValuePair("weather",cityName)};
        return RESTful.get(GOOGLE_WEATHER_URL).query(parmas).run().asString(CharEncodeUtil.EUC_KR);
    }
 

}
