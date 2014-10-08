package erwins.util.webapp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.Presence;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

import erwins.util.root.exception.IORuntimeException;
import erwins.util.text.StringUtil;

public abstract class GoogleXMPP {

	/** sendInvitation를 하면 바로 구글톡에 등록되버린다. 이후에 메세지 전달이 가능하다. */
	public static boolean send(String id, String message) {
		if (!StringUtil.contains(id, "@")) id += "@gmail.com";
		JID jid = new JID(id);
		return send(jid,message);
	}
	public static boolean send(JID jid, String message) {
		Message msg = new MessageBuilder().withRecipientJids(jid).withBody(message).build();
		XMPPService xmpp = XMPPServiceFactory.getXMPPService();
		Presence presence = xmpp.getPresence(jid);
		if(!presence.isAvailable()) xmpp.sendInvitation(jid);
		if(!presence.isAvailable()) return false;
		SendResponse status = xmpp.sendMessage(msg);
		return status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS;
	}
	
	public static Message parse(HttpServletRequest req) {
		XMPPService xmpp = XMPPServiceFactory.getXMPPService();
		try {
			return xmpp.parseMessage(req);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

}
