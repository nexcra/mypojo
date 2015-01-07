package erwins.util.web;

import java.net.SocketTimeoutException;

import org.apache.commons.httpclient.NameValuePair;

import com.google.common.base.Splitter;

import erwins.util.text.StringUtil;

/** 
 * IP를 이용한 간이 국가 확인도구
 * 소스 테스트용임!!
 *  */
public class IpInfoFinder{

	private HttpClient3 client = new HttpClient3().get("http://www.ipipipip.net/data/index.php");

	protected String getCountry(String ip) throws SocketTimeoutException {
		NameValuePair[] query = new  NameValuePair[2];
    	query[0] = new NameValuePair("ln","ko");
    	query[1] = new NameValuePair("ip",ip);
    	String html = client.query(query).executeMethod().asString();
    	String country = "";
    	for(String line : Splitter.on('\n').split(html)){
    		if(StringUtil.contains(line, "국가")){
    			String[] tags = line.split(" ");
    			country = tags[tags.length-1];
    		}
    	}
		return country;
	}
    

}
