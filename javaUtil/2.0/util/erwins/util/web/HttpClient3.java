
package erwins.util.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.params.CookiePolicy;

import com.google.common.base.Preconditions;

import erwins.util.lib.FileUtil;
import erwins.util.text.StringEscapeUtil;

/**
 * 
 * commons - HTTP client 3.1 을 기반으로 한다.
 * 4.0 이상에서 많이 변경되었음으로 임시로 사용하자.
 * 
 * 세션당 하나씩 만들어라.
 * url을 한번만 지정후 쿼리를 변경하면서 재사용 가능하다.
 * releaseConnection / abort 가 필요시 추가
 * 
 * 고도화 하자. 로그인 해서 지속적으로 사용 가능하다.
 * HttpMethod 결과값의 302 는 리다이렉트 된것
 * @author sin
 */
public class HttpClient3{
	
	/** 보안에 위험하니 주의! */
	public static void initSsl(){
		Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
	}
    
    private final HttpClient client = new HttpClient();
    private HttpMethod method;
    private String encode = "UTF-8";
    
    //httpclient.getParams().setParameter("http.protocol.expect-continue", false);//HttpClient POST 요청시 Expect 헤더정보 사용 x
    //httpclient.getParams().setParameter("http.connection.timeout", useTimeout * 1000);// 원격 호스트와 연결을 설정하는 시간
    //httpclient.getParams().setParameter("http.socket.timeout",  useTimeout * 1000);//데이터를 기다리는 시간
    //httpclient.getParams().setParameter("http.connection-manager.timeout",  useTimeout * 1000);// 연결 및 소켓 시간 초과 
    //httpclient.getParams().setParameter("http.protocol.head-body-timeout",  useTimeout * 1000);
    
    /** 부가 설정을 해준다. 이 메소드는 차후 수정  위 주석된 설정 참고
     * 내부적으로 어떻게 타임아웃을 주는지 모르겠다. 프로토콜 옵션인지? 아니면 소스상에 박혀있는건지?
     * ex) timeoutSec 5초  */
    public HttpClient3 setHttpConnectionManager(Integer timeoutSec) {
        HttpConnectionManager manager = new SimpleHttpConnectionManager();
        HttpConnectionManagerParams connectionParam = new HttpConnectionManagerParams();
        if(timeoutSec!=null) {
        	connectionParam.setConnectionTimeout(timeoutSec * 1000); // 원격 호스트와 연결을 설정하는 시간
        	connectionParam.setSoTimeout(timeoutSec * 1000); //데이터를 기다리는 시간
        }
        
        manager.setParams(connectionParam);
        client.setHttpConnectionManager(manager);
        
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        return this;
    }
    
    /** 예외 사항을 기술하기 위해서 쓴다. 
     * ex) client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY); (rfc 2109 domain must start with a dot)
     *  */
    public HttpClient getClient(){
    	return client;
    }
    
    public HttpClient3 get(String url){
    	method = new  GetMethod(url);
        return this;
    }
    
    public HttpClient3 post(String url){
    	method = new  PostMethod(url);
        return this;
    }
    
    /** 타임아웃이 설정된 경우 SocketTimeoutException를 던진다 */
    public HttpClient3 executeMethod() throws SocketTimeoutException{
    	try {
            client.executeMethod(method);
            return this;
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 파일 업로드시.
     * ex) FilePart filePart = new FilePart("upfile", targetFile, null, "EUC-KR"); //EUC-KR
     * Part[] parts = { filePart  };
     */
    public void upload(Part ... parts){
    	try {
    		PostMethod post = (PostMethod)method;
			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    public String asString(){
        String result = null;
        InputStream in = null;
        try {
        	in = method.getResponseBodyAsStream();
            result = IOUtils.toString(in,encode);
        }
        catch (IOException e) {
        	throw new RuntimeException(e);
        }finally{
            IOUtils.closeQuietly(in);
            method.releaseConnection();
        }
        return result;
    }
    
    public void asFile(File result){
    	InputStream in = null;
        try {
            in = method.getResponseBodyAsStream();
            FileUtil.write(in, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            IOUtils.closeQuietly(in);
            method.releaseConnection();
        }
    }
    
    /** in을 닫아야 한다? */
    public InputStream asStream(){
        InputStream in = null;
        try {
            in =  method.getResponseBodyAsStream();
        }catch (IOException e) {
        	throw new RuntimeException(e);
        }
        return in;
    }

    public String getEncode() {
        return encode;
    }
    public void setEncode(String encode) {
        this.encode = encode;
    }
    
	// ==================  쿼리만 교체, 아마 post만 되는듯 ===================
    
    public HttpClient3 query(Map<String,String> map) {
    	Preconditions.checkState(method instanceof PostMethod,"post  only");
    	PostMethod post = (PostMethod) method;
    	for(Entry<String, String> each : map.entrySet()){
    		post.addParameter(each.getKey(), each.getValue());
    	}
    	return this;
    }
    
    public HttpClient3 query(NameValuePair[] querys) {
        method.setQueryString(querys);
        return this;
    }
    public HttpClient3 query(String queryString) {
        if(StringUtils.isEmpty(queryString)) return this;
        method.setQueryString(queryString);
        return this;
    }
    
    /** 문자열에 =나 &가 들어가지 않아야 한다. -> 임시로직 */
    public static NameValuePair[] stringToParameter(String parameter){
        String[] eachParams = parameter.split("&");
        NameValuePair[] querys = new NameValuePair[eachParams.length];
        for(int i=0;i<eachParams.length;i++){
            String[] keyValue = eachParams[i].split("=");
            String value = keyValue.length > 1 ? keyValue[1] : "";
            value = StringEscapeUtil.unescapeUrl(value, "UTF-8");
            querys[i] = new NameValuePair(keyValue[0],value);
        }
        return querys;
    }

}
