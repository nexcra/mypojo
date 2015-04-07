
package erwins.util.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import lombok.Data;
import lombok.experimental.Delegate;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import erwins.util.lib.FileUtil;
import erwins.util.root.exception.IORuntimeException;

/**
 * commons - HTTP client 4 을 기반으로 기능 추가
 * 아직 타임아웃은 테스트 못함
 */
public class HttpClient4{
	
	private static final int DEFAULT_TIMEOUT_SEC = 60;

	/** InternalHttpClient로 생 */
	@Delegate
	private CloseableHttpClient httpclient;
	
	public HttpClient4(){
		this(DEFAULT_TIMEOUT_SEC);
	}
	
	public HttpClient4(int timeoutSec){
		RequestConfig config = RequestConfig.custom().setSocketTimeout(timeoutSec * 1000).setConnectTimeout(timeoutSec * 1000).build();
		httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();
	}
	
	/**
	 * List<NameValuePair> nvps = new ArrayList <NameValuePair>();
       nvps.add(new BasicNameValuePair("IDToken1", "username"));
       nvps.add(new BasicNameValuePair("IDToken2", "password"));
	 *  */
	public HttpResult post(String url,Charset encoding,List<NameValuePair> nvps) {
		HttpPost httpost = new HttpPost(url);
        httpost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
        return executeAndGetString(httpost,encoding);
	}
	
	public HttpResult post(String url,Charset encoding,String body)  {
		HttpPost httpost = new HttpPost(url);
        httpost.setEntity(new StringEntity(body,encoding));
        return executeAndGetString(httpost,encoding);
	}
	
	/** 
	 * ex) URI uri = new URIBuilder().setScheme("http").setHost("www.google.com").setPath("/search")
             .setParameter("q", "httpclient").setParameter("btnG", "Google Search").setParameter("aq", "f").setParameter("oq", "").build();
	 * */
	public HttpResult get(String url,Charset encoding)  {
        return executeAndGetString(new HttpGet(url),encoding);
	}
	
	
	/** 인코딩의 경우 헤더(getContentType 등)에 있긴 하지만..  부정확한 애가 더 많은듯.. 강제로 변환한다. */
	public HttpResult executeAndGetString(HttpRequestBase http,Charset encoding) {
		CloseableHttpResponse response= null;
		try {
			response = httpclient.execute(http);
			InputStream in =  response.getEntity().getContent();
			String result = null;
			try {
				result = IOUtils.toString(in,encoding);
			} finally {
				IOUtils.closeQuietly(in);
			}
			return new HttpResult(response,result);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IOUtils.closeQuietly(response);
		}
	}
	
	public CloseableHttpResponse executeAndGetFile(HttpRequestBase http,File file) {
		CloseableHttpResponse response= null;
		try {
			response = httpclient.execute(http);
			InputStream in =  response.getEntity().getContent();
			try {
				FileUtil.write(in, file);
			} finally {
				IOUtils.closeQuietly(in);
			}
			return response;
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			IOUtils.closeQuietly(response);
		}
	}
	
	/** 결과 문자열과 헤더 등을 같이 보기위함 */
	@Data
	public static class HttpResult{
		private final CloseableHttpResponse response;
		private final String text;
	}

}
