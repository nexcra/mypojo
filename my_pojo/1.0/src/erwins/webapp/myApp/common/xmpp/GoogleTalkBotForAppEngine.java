
package erwins.webapp.myApp.common.xmpp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import erwins.util.lib.StringUtil;
import erwins.util.webapp.GoogleXMPP;
import erwins.webapp.myApp.OpenApiContainer;

@Component
public class GoogleTalkBotForAppEngine extends GoogleTalkBot{
	
	
	@Autowired private OpenApiContainer openApiContainer;
	
	@Override
	protected void sendMessage(String mailAdress,String message){
		System.out.println(message);
		GoogleXMPP.send(mailAdress, message);
	}
    
	final Token WEATHER = tokens.add("w", "날씨", "현재시각기준 서울의 날씨를 알려드립니다.", new MessageTo() {
		public void send(String mailAdress, String message) {
			sendMessage(mailAdress, StringUtil.join(openApiContainer.weatherSimpleCast(), "\n"));
		}
	});

}
