package erwins.webapp.myApp.common.xmpp;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.google.appengine.api.xmpp.Message;

import erwins.util.webapp.GoogleXMPP;
import erwins.webapp.myApp.AjaxView;

@Controller
public class XMPPController {
	
	@Resource GoogleTalkBotForAppEngine googleTalkBot;
	
	@RequestMapping("/_ah/xmpp/message/chat/")
	public View userStatistics(HttpServletRequest req) {
		Message msg = GoogleXMPP.parse(req);
		googleTalkBot.parseAndSend(msg.getFromJid().getId(),msg.getBody());
		return new AjaxView(""); //무시된다.
	}
	
	@RequestMapping("none/n")
	public View test(HttpServletRequest req) {
		googleTalkBot.parseAndSend("my.pojo","a 0.3");
		return new AjaxView(""); //무시된다.
	}
}
