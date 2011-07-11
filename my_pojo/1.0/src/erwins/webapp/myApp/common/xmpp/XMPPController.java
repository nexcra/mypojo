package erwins.webapp.myApp.common.xmpp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.google.appengine.api.xmpp.Message;

import erwins.util.webapp.GoogleXMPP;
import erwins.webapp.myApp.AjaxView;

@Controller
public class XMPPController {
	
	@RequestMapping("/_ah/xmpp/message/chat/")
	public View userStatistics(HttpServletRequest req) {
		Message msg = GoogleXMPP.parse(req);
		GoogleXMPP.send(msg.getFromJid(), msg.getBody() + " : 명령어가 리턴되었습니다. 아직 미지원 기능입니다.");
		return new AjaxView(""); //무시된다.
	}
}
