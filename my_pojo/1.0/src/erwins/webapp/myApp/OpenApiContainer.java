package erwins.webapp.myApp;

import org.springframework.stereotype.Controller;

import erwins.util.openApi.GoogleXmlParser;
import erwins.util.temp.UrlConnection;

/** 오픈API를 모아두자. 하지만 지금은 구글뿐 */
@Controller
public class OpenApiContainer {
	
	private UrlConnection urlConnection = new UrlConnection();
	
	public String weatherIsRain() {
		String xml = weather();
		return (String)new GoogleXmlParser(xml).isRain();
	}
	
	public String[] weatherSimpleCast() {
		String xml = weather();
		return (String[])new GoogleXmlParser(xml).simpleCast();
	}

	private String weather() {
		return urlConnection.doGet("http://www.google.co.kr/ig/api?weather=seoul");
	}
	

}
