package erwins.test.domain.talk.client;

import java.util.ArrayList;
import java.util.List;

import erwins.domain.talk.Protocol;

/** 이놈은 따로 테스트 하지 않는다. */
public class ClientrRunnerTest {
	
	private static final int PORT = 9988;
	private static final String HOST = "165.124.200.163";
	
	/** 동시성 테스트~ */
	public static void main(String[] args) {
		
		List<TalkClientForConsole> list = new ArrayList<TalkClientForConsole>();
		int size = 20;
		for(int i=0;i<size;i++){
			TalkClientForConsole client = new TalkClientForConsole(HOST,PORT);
			client.testStartup();
			list.add(client);
			client.sendToServer(Protocol.LOGIN,"테스트"+i);
			stop(10);
		}
		
		for(TalkClientForConsole each : list){
			each.sendToServer(Protocol.MESSAGE,"벌크 메세지 전송1");
			each.sendToServer(Protocol.MESSAGE,"벌크 메세지 전송2");
			stop(10);
		}
		
		stop(1000);
		
		for(TalkClientForConsole each : list){
			each.shutdown();
		}
		
		
	}

	

	private static void stop(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			
		}
	}

}
