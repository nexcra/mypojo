
package erwins.util.openApi;

import org.junit.Before;

import erwins.util.exception.runtime.Val;

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
        Val.isNotEmpty(Google.getWeatherHtmlTable("inchon"));
    }
    
    @org.junit.Test
    public void engDic(){
        Val.isNotEmpty(Naver.getEnglishDicUrl("never"));
    }

}
