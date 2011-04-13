package erwins.test.domain.talk.client;

import java.util.ArrayList;
import java.util.List;

import erwins.domain.talk.BroadcastMessage;
import erwins.domain.talk.Protocol;
import erwins.util.nio.MessageCallback;
import erwins.util.root.PairObject;
import erwins.util.vender.apache._Log;
import erwins.util.vender.apache._LogFactory;

/** 테스트용이다. */
public class ConsoleMessageCallback implements MessageCallback{
	
	protected _Log log = _LogFactory.instance(this.getClass());
	public List<PairObject> logList = new ArrayList<PairObject>();
	
	@Override
	public void messageFromServer(String message) {
		String[] messages = Protocol.splitMessage(message);
		for(String eachMessage : messages){
			String[] text = Protocol.splitHeader(eachMessage);
			if(text.length!=2) throw new RuntimeException("text must be array!!! @_@;");
			executeMessage(text[0],text[1]);	
		}
	}
	
	/** 테스트를 위해 protected로 햇다. */
	private void executeMessage(String header,String textMessage) {
		logList.add(new PairObject(header,textMessage));
		if(header.equals(Protocol.LOGIN)) log.debug("{0} 님께서 로그인하셨습니다.", textMessage);
		else if(header.equals(Protocol.MESSAGE)){
			BroadcastMessage message = new BroadcastMessage();
			message.parsText(textMessage);
			log.debug("{0}님의 말 : {1}", message.getSenderId(),message.getMessage());
		}else if(header.equals(Protocol.LOGIN_INFO)){
			log.debug("로그인정보 : {0}", textMessage);
		}else if(header.equals(Protocol.LOGOUT)){
			log.debug("{0} 님이 로그아웃 했습니다.", textMessage);
		}else if(header.equals(Protocol.EXIT)){
			System.out.println(textMessage + " 님의 연결이 끊어졌습니다.");
			log.debug("{0} 님의 연결이 끊어졌습니다.", textMessage);
		}else if(header.equals(Protocol.ERROR_LOGIN_EXIST)){
			log.debug("{0} 는 이미 로그인하셨습니다.", textMessage); //이건 나오면 안되긔~
		}else{
			log.debug("Error : {0} / {1}",header, textMessage);
		}
	}



}
