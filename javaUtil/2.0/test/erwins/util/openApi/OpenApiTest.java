
package erwins.util.openApi;

import org.junit.Before;

import erwins.util.exception.Check;

public class OpenApiTest {
    
    @Before
    public void init(){
    	
    }

    @org.junit.Test
    public void geocode(){
        Check.isNotEmpty(Naver.getGeoCode("구로"));
    }
    
    @org.junit.Test
    public void weather(){
        String xml = Google.getWeatherXml("");
        Check.isNotEmpty(xml);
        Check.isNotEmpty(new GoogleXmlParser(xml).buildHtmlTable());
    }

}
