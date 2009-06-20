
package erwins.util.openApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.ecs.html.Script;

import erwins.util.dom2.Code;
import erwins.util.tools.SystemInfo;
import erwins.util.vender.apache.ECS2;
import erwins.util.xml.DocParser;


/**
 * 네이버의 각종 API에 관한 결과를 래핑한다.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class Naver{
    
    
    /**
     * 제공 : naver.com <br>
     * String으로 네이버 영어사전의 주소를 가져온다.
     */
    public static String getEnglishDicUrl(String eng){
        
        List<NameValuePair> parmas = new ArrayList<NameValuePair>();
        parmas.add(new NameValuePair("key",NAVER_API_KEY));
        parmas.add(new NameValuePair("query",eng));
        parmas.add(new NameValuePair("display","5"));
        parmas.add(new NameValuePair("start","1"));
        parmas.add(new NameValuePair("target","endic"));
        
        String xml = RestConnector.getXml(NAVER_ENG_DIC_URL,parmas);
        
        return xml;
    }
    
    /**
     * 제공 : naver.com <br>
     * 네이버 영어사전 주소로 사전 내용물 String을 가져온다.
     */
    public static String getEnglishDicByUrl(String id){
        List<NameValuePair> parmas = new ArrayList<NameValuePair>();
        parmas.add(new NameValuePair("docid",id));
        
        String value = RestConnector.getXml(NAVER_ENG_DIC_CONTENT_URL,parmas);
        
        int top = value.indexOf("<!-- 뜻풀이-->");
        int bo = value.indexOf("<!-- //뜻풀이-->");
        return value = value.substring(top,bo);
    }    
    
    /**
     * 지오코딩 결과를 HTML Option으로 제공한다.
     */
    public static String getGeoCode(String address){
        
        String query;
        try {
            query = "key="+GetNaverMapKey()+"&query="+URLEncoder.encode(address,"euc-kr");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("runtime fail");
        }

        DocParser dp = new DocParser(RestConnector.getXml(NAVER_GEOCODE_URL,query));
        List<HashMap<String,String>> results =  dp.getElementsByTagName("x","y","address") ;
        
        List<Code> doms = new ArrayList<Code>();
        
        Code dom = new Code();
        dom.setId("");
        dom.setName("원하시는 지역을 골라주세요");
        doms.add(dom);
        
        
        for(Map<String,String> result : results){
            dom = new Code();
            dom.setId(result.get("x")+","+result.get("y"));
            dom.setName(result.get("address"));
            doms.add(dom);
        }
        return ECS2.OPTION.get(doms);
        
    }

    //URL
    private static final String NAVER_GEOCODE_URL = "http://maps.naver.com/api/geocode.php";
    private static final String NAVER_ENG_DIC_URL = "http://openapi.naver.com/search";
    private static final String NAVER_ENG_DIC_CONTENT_URL = "http://endic.naver.com/endic.naver";
    /** 일반Key */
    private static final String NAVER_API_KEY = "3826a877cfff565aa2f109ad9a9c121d";
    //지도 관련
    @Deprecated
    private static final String NAVER_MAP_OLD_JS_URL = "http://maps.naver.com/js/naverMap.naver";
    private static final String NAVER_MAP_JS_URL = "http://map.naver.com/js/naverMap.naver";
    private static final String NAVER_LOCAL_MAP_KEY = "ac7298e783b0c0ebced2520878d43c35";
    private static final String NAVER_SERVER_MAP_KEY = "f792ae611c9e8df4018443e56edb53c4";
    
    /**
     * 지정된 서버IP가 아니면 LOCAL Key를 , 지정된 서버이면 서버 key를 리턴한다.
     */    
    public static String GetNaverMapKey(){
        return SystemInfo.isServer() ? NAVER_SERVER_MAP_KEY : NAVER_LOCAL_MAP_KEY;
    }
    /**
     * 주소 변경시 일괄 적용 위함 
     */    
    public static String getNaverScript(){
        Script wqe = new Script();
        wqe.setSrc(NAVER_MAP_JS_URL+"?key="+GetNaverMapKey());
        wqe.setType("text/JavaScript");
        return wqe.toString();
    }
   

}
