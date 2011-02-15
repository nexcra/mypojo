
package erwins.util.webapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import erwins.util.tools.StringBuilder2;


public abstract class AppUtil{

	/** 직접 커넥션을 줄 수 없어서 이렇게 REST자료를 얻어와야 한다. */
	public String rest(String urlString){
		try {
			URL url = new URL(urlString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder2 b = new StringBuilder2(); 
			String line;
			while((line=reader.readLine())!=null){
				b.appendLine(line);
			}
			reader.close();
			return b.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
