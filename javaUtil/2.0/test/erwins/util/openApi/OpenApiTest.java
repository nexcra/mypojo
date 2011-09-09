
package erwins.util.openApi;

import erwins.util.exception.Check;

public class OpenApiTest {
    
    @org.junit.Test
    public void geocode(){
        Check.isNotEmpty(Naver.getGeoCode("구로"));
    }
    
    @org.junit.Test
    public void weather(){
        String xml = Google.getWeatherXml("");
        System.out.println(xml);
        Check.isNotEmpty(xml);
        Check.isNotEmpty(new GoogleXmlParser(xml).buildHtmlTable());
    }

}
