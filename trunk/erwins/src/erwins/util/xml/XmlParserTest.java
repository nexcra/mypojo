
package erwins.util.xml;

import org.junit.Test;

import erwins.util.exception.runtime.Val;



public class XmlParserTest{
    
    @Test
    public void testWrite(){
        
        String xmlDocumentStr = "<?xml version=\"1.0\"?><xml_api_reply version=\"1\"><test qwe='dd'>zzzz</test><weather module_id=\"0\" tab_id=\"0\"><forecast_information><city data=\"inchon\"/><postal_code data=\"inchon\"/><latitude_e6 data=\"\"/><longitude_e6 data=\"\"/><forecast_date data=\"2008-12-18\"/><current_date_time data=\"2008-12-17 23:35:08 +0000\"/><unit_system data=\"SI\"/></forecast_information><current_conditions><condition data=\"맑음\"/><temp_f data=\"30\"/><temp_c data=\"-1\"/><humidity data=\"습도: 59%\"/><icon data=\"/images/weather/sunny.gif\"/><wind_condition data=\"바람: 서풍, 3 km/h\"/></current_conditions><forecast_conditions><day_of_week data=\"목\"/><low data=\"-2\"/><high data=\"2\"/><icon data=\"/images/weather/sunny.gif\"/><condition data=\"맑음\"/></forecast_conditions><forecast_conditions><day_of_week data=\"금\"/><low data=\"-1\"/><high data=\"6\"/><icon data=\"/images/weather/mostly_sunny.gif\"/><condition data=\"대체로 맑음\"/></forecast_conditions><forecast_conditions><day_of_week data=\"토\"/><low data=\"-2\"/><high data=\"2\"/><icon data=\"/images/weather/mostly_sunny.gif\"/><condition data=\"구름 조금\"/></forecast_conditions><forecast_conditions><day_of_week data=\"일\"/><low data=\"-11\"/><high data=\"0\"/><icon data=\"/images/weather/chance_of_snow.gif\"/><condition data=\"한때 눈\"/></forecast_conditions></weather></xml_api_reply>";       
        
        XmlParser p = new XmlParser(xmlDocumentStr);
        
        Val.isTrue(p.get("test").tagText.equals("zzzz"));
        Val.isTrue(p.get("humidity").attribute.get("data").equals("습도: 59%"));
        Val.isTrue(p.gets("icon").get(3).attribute.get("data").equals("/images/weather/mostly_sunny.gif"));
        
    }
    


}
