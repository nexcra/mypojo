
package erwins.util.openApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;



/**
 * HTTP Client로 각 오픈API와 통신한다. 여기에 인증키와 통신방법을 기술한다.  <br>
 * 결과는 각 벤더별 클래스에서 처리한다. <br>
 * NameValuePair 사용시 자동으로 UTF-8형식으로 URL-인코드 된다?.
 * 표준파서 사용시 Stream을 그대로 부어서 제작하면 encode오류가 난다. ㅠㅠ 이유는 몰라염 내공이 부족하구나.
 * 따라서 getResponseBodyAsString()으로 String변환(즉 UTF-8)했다가 stream으로 바꾸어 준다.
 * 근데 위방법도 같은 XP장비의 톰캣에서 에러가 난다..  그래서 파서를 하나 만들었다.
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class RestConnector{
    
    // ===========================================================================================
    //                                  Naver
    // ===========================================================================================
    
    private static HttpClient client = new HttpClient();

    
    /**
     * NameValuePair를 이용해 XML을 리턴한다.
     */
    public static String getXml(String url,List<NameValuePair> parmas){
        PostMethod method = new  PostMethod(url);
        method.setQueryString(parmas.toArray(new NameValuePair[parmas.size()]));
        try {
            client.executeMethod(method);
            return method.getResponseBodyAsString();
        }
        catch (HttpException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * queryString을 이용해 XML을 리턴한다.
     * 특이하게 EUC-KR을 사용할때 사용한다.
     * 따라서 NameValuePair을 사용하지 말고 따로 인코딩을 해주자. 
     */
    public static InputStream getXml(String url,String queryString){
        PostMethod method = new  PostMethod(url);
        method.setQueryString(queryString);
        try {
            client.executeMethod(method);
            return method.getResponseBodyAsStream();
        }
        catch (HttpException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
