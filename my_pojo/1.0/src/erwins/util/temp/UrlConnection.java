package erwins.util.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

/** 엡엔진용 rest api. 딴건 다 제한 걸려서 새로 만들었다. */
public class UrlConnection{
	
	private String encode = "euc-kr";
	
	public UrlConnection(){
		
	}
	
	
	public String doGet(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),encode));
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String line = IOUtils.toString(reader);
			    IOUtils.closeQuietly(reader);
			    return line;
			} else {
				return null; //나중에 수정 
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (ProtocolException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String doPost(String urlString,Map<String,Object> map) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			for(Entry<String,Object> each : map.entrySet()){
				connection.setRequestProperty(each.getKey(), each.getValue().toString());	
			}
			connection.setDoOutput(true);
			connection.connect();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),encode));
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String line = IOUtils.toString(reader);
				IOUtils.closeQuietly(reader);
				return line;
			} else {
				return null; //나중에 수정 
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (ProtocolException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

}
