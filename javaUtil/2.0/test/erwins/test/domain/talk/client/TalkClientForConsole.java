package erwins.test.domain.talk.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;

import erwins.domain.talk.Protocol;
import erwins.util.nio.TalkClient;
import erwins.util.root.PairObject;
public class TalkClientForConsole extends TalkClient{
	
	public TalkClientForConsole(String adress,int port){
		super(adress, port);
	}
	
	public List<PairObject> testStartup() {
		ConsoleMessageCallback callback = new ConsoleMessageCallback();
		messageStartup(callback);
		return callback.logList;
	}
	
	public void sendToServer(String header,String message){
		sendToServer(Protocol.newMessage(header, message));
	}
	
	private Thread inputThread;
	
	@Override
	public void shutdown(){
		if(inputThread!=null) inputThread.interrupt();
		super.shutdown();
	}

	public void waitForKeyboardinput() {
		Runnable target = new Runnable() {
			@Override
			public void run() {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				try {
					while(!Thread.currentThread().isInterrupted()){
						String message = in.readLine();
						if(message.equals("logout")){
							sendToServer(Protocol.LOGOUT,message);
							shutdown();
						}else sendToServer(Protocol.MESSAGE,message);
						System.out.println("Send : " + message);
					}
					System.out.println("클라이언트를 종료합니다.");
				}catch (IOException e) {
					throw new RuntimeException(e);
				}finally{
					IOUtils.closeQuietly(in);
				}
				
			}
		};
		inputThread = new Thread(target);
		inputThread.start();
	}

}
