
package erwins.util.openApi;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.ecs.html.Script;
import org.apache.ecs.html.Table;
import org.apache.ecs.wml.*;

import erwins.util.dom2.Code;
import erwins.util.lib.Strings;
import erwins.util.tools.SystemInfo;
import erwins.util.xml.XmlParser;


/**
 * GoogleWeather를 래핑한다.
 * @author erwins(my.pojo@gmail.com)
 */
public class Google{
    
    private static final String GOOGLE_MAP_URL = "http://maps.google.com/maps";
    private static final String GOOGLE_CHART_URL = "http://chart.apis.google.com/chart";
    private static final String GOOGLE_WEATHER_URL = "http://www.google.co.kr/ig/api";
    private static final String GOOGLE_LOCAL_MAP_KEY = "ABQIAAAAbnojiUetDB2JPnAr7msMxhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSSUTahXG1TgB4AL-LL3DXVwdGvXg";
    private static final String GOOGLE_SERVER_MAP_KEY = "ABQIAAAAbnojiUetDB2JPnAr7msMxhSJ9_8WeQAaXDHeBdc7dwvnuUSw7BQOdEokh8Ck27mfaJWo36FaoQVUpQ";
    
    /**
     * 지정된 서버IP가 아니면 LOCAL Key를 , 지정된 서버이면 서버 key를 리턴한다.
     */
    public static String GetGoogleMapKey(){
        return SystemInfo.isServer() ? GOOGLE_SERVER_MAP_KEY : GOOGLE_LOCAL_MAP_KEY;
    }

    /**
     * 주소 변경시 일괄 적용 위함 
     */
    public static String getGoogleScript(){
        Script js = new Script();
        js.setSrc(GOOGLE_MAP_URL+"?file=api&amp;v=2&amp;key="+GetGoogleMapKey());
        js.setType("text/JavaScript");
        return js.toString();
    }
    
    // ===========================================================================================
    //                                   API
    // ===========================================================================================    
    
    /**
     * @author     Administrator
     */
    private static enum ChartMode{
        /**
         * @uml.property  name="p3"
         * @uml.associationEnd  
         */
        p3;
        public String get(){
            return "cht=" + this.toString();
        }
    }
    
    /**
     * 구글 차트 정보를 img태그로 리턴한다.
     */
    public static String getChart(Map<Object,Object> map,int width,int height){
        String size = MessageFormat.format("chs={0}x{1}", width,height);
        List<String> label = new ArrayList<String>();
        List<String> value = new ArrayList<String>();
        
        for(Object key : map.keySet()){
            label.add(key.toString() +"("+ map.get(key).toString()+")");
            value.add(map.get(key).toString());
        }
        String a = "chl=" + Strings.joinTemp(label,"|");
        String b = "chd=t:" + Strings.joinTemp(value,",");
        
        Img img = new Img(GOOGLE_CHART_URL+"?"+size+"&"+a+"&"+b+"&"+ChartMode.p3.get());
        return img.toString();
    }
    
    /**
     * 날씨 부분만 리턴한다. 5개의 배열로 구성되며 3일 후까지 체크된다.
     */
    public static String[] getWeatherStr(String city){
        String cityName = Strings.nvl(city,"seoul");
        Code dom = Code.getElementById(city);
        if(dom!=null) cityName = dom.getParam1();
        
        List<NameValuePair> parmas = new ArrayList<NameValuePair>();
        parmas.add(new NameValuePair("weather",cityName));
        
        XmlParser dp = new XmlParser(RestConnector.getXml(GOOGLE_WEATHER_URL, parmas));
        
        String[] weather = new String[5];
        
        for(int i=0;i<5;i++) weather[i] = dp.gets("condition").get(i).get();
        
        return weather;
        
    }
    
    /**
     * 구글 날씨 정보를 HTML table로 만들어 리턴한다.
     */
    public static String getWeatherHtmlTable(String city){
        String cityName = Strings.nvl(city,"seoul");
        Code dom = Code.getElementById(city);
        if(dom!=null) cityName = dom.getParam1();
        
        List<NameValuePair> parmas = new ArrayList<NameValuePair>();
        parmas.add(new NameValuePair("weather",cityName));
        
        XmlParser dp = new XmlParser(RestConnector.getXml(GOOGLE_WEATHER_URL, parmas));
        int size = dp.gets("icon").size();
        
        Table t = new Table();
        t.setClass("box");
        Tr tr0 = new Tr();
        Tr tr1 = new Tr();
        Tr tr2 = new Tr();
        Tr tr3 = new Tr();
        Tr tr4 = new Tr();
        t.addElement(tr0);
        t.addElement(tr1);
        t.addElement(tr2);
        t.addElement(tr3);
        t.addElement(tr4);
        
        String nowTime = dp.get("current_date_time").get().substring(11,11+5);
        Td topData = new Td(cityName +" , 측정시각 : "+ nowTime);
        tr0.addElement(topData);
        topData.addAttribute("colspan",size);
        
        Td bottomData = new Td(dp.get("humidity").get() + dp.get("wind_condition"));        
        bottomData.addAttribute("colspan",size);
        tr4.addElement(bottomData);
        
        //제목달기
        for(int i=0;i<size;i++){
            if(i==0) tr1.addElement(new Td("측정시"));
            else tr1.addElement(new Td(dp.gets("day_of_week").get(i-1).get()));
            tr2.addElement(new Td(new Img("http://www.google.co.kr"+ dp.gets("icon").get(i).get())));
            tr3.addElement(new Td(dp.gets("condition").get(i).get()));
        }
        
        return t.toString();
    }

}
