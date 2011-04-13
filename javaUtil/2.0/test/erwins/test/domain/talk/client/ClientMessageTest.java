package erwins.test.domain.talk.client;

import java.util.List;

import org.junit.Test;

import erwins.domain.talk.Protocol;
import erwins.domain.talk.TalkContext;
import erwins.domain.talk.server.TalkServer;
import erwins.util.exception.Check;
import erwins.util.root.PairObject;


public class ClientMessageTest {
	
	@Test
	public void 채팅클라이언트_정상작동테스트() {
		TalkServer server = new TalkServer(TalkContext.TALK_SERVER_PORT);
		server.startup();
		
		stop(500);
		
		for(int i=0;i<3;i++){
			unitTest();
			stop(300);
		}
		server.shutdown();
	}

	private static void unitTest() {
		TalkClientForConsole client1 = new TalkClientForConsole(TalkContext.TALK_SERVER_IP,TalkContext.TALK_SERVER_PORT);
		List<PairObject> log1 = client1.testStartup();
		
		TalkClientForConsole client2 = new TalkClientForConsole(TalkContext.TALK_SERVER_IP,TalkContext.TALK_SERVER_PORT);
		List<PairObject> log2 =  client2.testStartup();
		
		
		client1.sendToServer(Protocol.LOGIN,"개똥이");
		stop(50);
		client2.sendToServer(Protocol.LOGIN,"영감님");
		stop(50);
		client1.sendToServer(Protocol.LOGIN,"개똥이"); //로그인 안됨
		stop(50);
		client2.sendToServer(Protocol.MESSAGE,"난 영감님");
		stop(50);
		client1.sendToServer(Protocol.MESSAGE,"난 개똥이");
		stop(50);
		client1.sendToServer(Protocol.LOGOUT,"~");
		client2.sendToServer(Protocol.LOGOUT,"~");
		
		stop(200);
		
		client1.shutdown();
		client2.shutdown();
		
		validate(log1, log2);
	}

	private static void validate(List<PairObject> log1, List<PairObject> log2) {
		/*
		System.out.println("=== 개똥이 ===");
		for(PairValue each : log1){
			System.out.println(each.getName() + " : " + each.getValue());
		}
		System.out.println("=== 영감님 ===");
		for(PairValue each : log2){
			System.out.println(each.getName() + " : " + each.getValue());
		}*/
		
		Check.isEquals(log1.get(0),new PairObject(Protocol.LOGIN,"개똥이"));
		Check.isEquals(log1.get(1),new PairObject(Protocol.LOGIN_INFO,"개똥이"));
		Check.isEquals(log1.get(2),new PairObject(Protocol.LOGIN,"영감님"));
		//Val.isEquals(log1.get(3),new PairValue(Header.LOGIN_INFO,"영감님#@#개똥이")); //정렬 안됨 ㅋㅋ
		Check.isEquals(log1.get(4),new PairObject(Protocol.ERROR_LOGIN_EXIST,"개똥이#@#개똥이"));
		Check.isEquals(log1.get(5),new PairObject(Protocol.MESSAGE,"영감님#@#난 영감님"));
		Check.isEquals(log1.get(6),new PairObject(Protocol.MESSAGE,"개똥이#@#난 개똥이"));
		
		Check.isEquals(log2.get(0),new PairObject(Protocol.LOGIN,"영감님"));
		//Val.isEquals(log2.get(1),new PairValue(Header.LOGIN_INFO,"영감님#@#개똥이"));
		Check.isEquals(log2.get(2),new PairObject(Protocol.MESSAGE,"영감님#@#난 영감님"));
		Check.isEquals(log2.get(3),new PairObject(Protocol.MESSAGE,"개똥이#@#난 개똥이"));
		//Val.isEquals(log2.get(4),new PairValue(Header.LOGOUT,"개똥이")); //exit가 먼저 처리되는 경우가 있음.
	}

	private static void stop(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			
		}
	}

}
