
package erwins.util.openApi;

import org.junit.Before;

import erwins.util.exception.Val;

public class OpenApiTest {
    
    
    @Before
    public void init(){
    }

    @org.junit.Test
    public void geocode(){
        Val.isNotEmpty(Naver.getGeoCode("구로"));
    }
    
    @org.junit.Test
    public void weather(){
        String xml = Google.getWeatherXml("");
        Val.isNotEmpty(xml);
        Val.isNotEmpty(new GoogleXmlParser(xml).buildHtmlTable());
    }

}
