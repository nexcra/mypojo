
package erwins.util.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import erwins.util.exception.ExceptionUtil;
import erwins.util.lib.FileUtil;

/**
 * 세션당 하나씩 만들어라.
 * url을 한번만 지정후 쿼리를 변경하면서 재사용 가능하다.
 * @author sin
 */
public class HttpData{
    
    private final HttpClient client = new HttpClient();
    private HttpMethod method;
    
    private String encode = "UTF-8";
    private Integer timeoutSec ;
    private boolean post = true;
    
    /** 기본 설정의 인스턴스를 리턴한다. */
    public static HttpData getSimpleClient(){
    	HttpData instance = new HttpData();
    	instance.timeoutSec = 5;
    	instance.init();
    	return instance;
    }
    
    /** 객체 초기화 */
    public HttpData init() {
        HttpConnectionManager manager = new SimpleHttpConnectionManager();
        HttpConnectionManagerParams connectionParam = new HttpConnectionManagerParams();
        if(timeoutSec!=null) connectionParam.setConnectionTimeout(timeoutSec*1000);
        
        manager.setParams(connectionParam);
        client.setHttpConnectionManager(manager);
        
        if(post)  method = new  PostMethod();
        else method = new  GetMethod();
        return this;
    }
    
    public HttpData url(String url){
        try {
            method.setURI(new HttpURL(url));
        } catch (URIException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    
    public String send(){
        try {
            client.executeMethod(method);
            return asString(encode);
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
    
    public String asString(String encode){
        String result = null;
        InputStream in = null;
        try {
        	in = method.getResponseBodyAsStream();
            result = IOUtils.toString(in,encode);
        }
        catch (IOException e) {
            ExceptionUtil.castToRuntimeException(e);
        }finally{
            IOUtils.closeQuietly(in);
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
        }
    }

    public String getEncode() {
        return encode;
    }
    public void setEncode(String encode) {
        this.encode = encode;
    }
    
    // ==================  쿼리만 교체, 아마 post만 되는듯 ===================
    public HttpData query(NameValuePair[] querys) {
        method.setQueryString(querys);
        return this;
    }
    public HttpData query(String queryString) {
        if(StringUtils.isEmpty(queryString)) return this;
        method.setQueryString(queryString);
        return this;
    }
    
    // =============== 이상한애들??
    
    /** get방식일때 사용한다. 문자열에 =나 &가 들어가지 않아야 한다. -> 임시로직 */
    public HttpData url(String url,String parameter){
        String[] eachParams = parameter.split("&");
        NameValuePair[] querys = new NameValuePair[eachParams.length];
        for(int i=0;i<eachParams.length;i++){
            String[] keyValue = eachParams[i].split("=");
            querys[i] = new NameValuePair(keyValue[0],keyValue[1]);
        }
        return url(url,querys);
    }
    
    /** get방식일때 사용한다. */
    public HttpData url(String url,NameValuePair[] querys){
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for(NameValuePair each : querys){
        	if(first) first = false;
        	else b.append("&"); 
            b.append(each.getName());
            b.append("=");
            //b.append(StringEscapeUtil.escapeUrl(each.getValue(), "UTF-8"));
            b.append(each.getValue());
        }
        return url(url+"?"+b.toString());
    }

}
