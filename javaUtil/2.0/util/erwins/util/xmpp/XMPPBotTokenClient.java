package erwins.util.xmpp;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.packet.XMPPError;

public class XMPPBotTokenClient extends XMPPBotClient {

	public XMPPBotTokenClient(String host, int port,String serverName, String id, String pass) {
		super(host, port,serverName, id, pass);
		setBotMessageListener(new XMPPBotTokenMessageListener());
	}
	
	public XMPPBotTokenClient(String id, String pass) {
		super(id, pass);
		setBotMessageListener(new XMPPBotTokenMessageListener());
	}
	
	/** 채팅 메세지가 오류로 나오면  자동으로 친구추가를 요청한다. */
	private boolean autoSubscribe = true;

	/** 일치하는 명령이 없으면 실행하지 않는다.
	 * 메제지의 경우 구독신청이 되어있지 않은경우에는 튕겨저 나온다. 이를 이벤트로 받아 답글을 쓸 경우 무한루프에 빠지게 된다. 
	 * 따라서  type이 chat인지 확인해 주자 */
	public class XMPPBotTokenMessageListener implements XMPPBotMessageListener{
		@Override
		public void message(Message from) {
			String fromId = from.getFrom().split("/")[0];
			if(from.getType() != Message.Type.chat){
                XMPPError e = from.getError();
                if(autoSubscribe) subscribe(fromId);
                else log.warn("구독 신청이 되어있느지 확인해주세요. --> error chat msg : " + e.getMessage());
                return;
            }
			String body = from.getBody();
			for(Token token:tokens){
				if(token.executeMessage(from,fromId)) return;
			}
			sendMessage(fromId, body);
		}
    }
	
	private List<Token> tokens = new ArrayList<Token>();

	/** 개별 토큰들. 한개의 명령만을 지원한다. */
	public static abstract class Token {
		private final String parse;
		public Token(String parse) {
			this.parse = parse;
		}

		/** 처리 성공시 true */
		public boolean executeMessage(Message from,String fromId) {
			String body = from.getBody();
			if (!body.startsWith(parse)) return false;
			message(from,fromId);
			return true;
		}
		protected abstract void message(Message from,String fromId);
	}
	
    public Token add(Token token){
        tokens.add(token);
        return token;
    }
    
    /** 임시로직.
     * 쌍방향 관계가 아닌 애들에게 구독 요청을 보낸다. */
	public void TEST_SubscribeAll() {
		for(RosterEntry each :  getEntries()){
			if(each.getType().equals(ItemType.from)) subscribe(each.getUser());
		}
	}
	
	public void TEST_info() {
		//createAccount(id, pass);
	}
	
	public void TEST_regroup() {
		for(RosterEntry each :  getEntries()){
			if(each.getGroups().size()==0){
				String id = each.getUser();
				if(id.startsWith("cctv")) addGroup(each,"CCTV");
				else if(id.startsWith("user")) addGroup(each,"USER");
			}
		}
	}

	public boolean isAutoSubscribe() {
		return autoSubscribe;
	}

	public void setAutoSubscribe(boolean autoSubscribe) {
		this.autoSubscribe = autoSubscribe;
	}    

}
