package erwins.jsample.domain.talk.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import erwins.jsample.domain.talk.Protocol;
import erwins.util.nio.TalkServerTemplate;
import erwins.util.nio.ThreadPool;
import erwins.util.validation.InputValidationException;

/** 기본 스래드와 별개 스래드간의 교차지점이다. */
public class TalkServer extends TalkServerTemplate{
	
	private final Set<SelectionKey> logined = Collections.synchronizedSet(new HashSet<SelectionKey>());
	private final BlockingQueue<TalkServerJob> messageQueue = new LinkedBlockingDeque<TalkServerJob>(); 
	
	private final ThreadPool threadPool;

	public TalkServer(int port) {
		super(port);
		threadPool = new ThreadPool();
		threadPool.add(new Thread(new TalkServerThread(logined,messageQueue))).setName("TalkServer_Thread-1");	
		threadPool.add(new Thread(new TalkServerThread(logined,messageQueue))).setName("TalkServer_Thread-2");	
		threadPool.startup();
		//log.debug("서버 기동");
	}

	/** 싱글 스래드에서 운영되며 TalkServerJob 하나에 다건의 메시지가 전송될 수 있다. 
	 * 스래드 안전함으로 add()로 입력한다.
	 * 로그아웃의 경우 즉시 처리해준다. */
	@Override
	protected void inputFromClient(final SelectionKey key,final String input) throws IOException {
		String[] messages = Protocol.splitMessage(input);
		for(String eachMessage : messages){
			String[] text = Protocol.splitHeader(eachMessage);
			if(text.length!=2) throw new InputValidationException("[{0}] : 헤더 오류",eachMessage);
			if(Protocol.LOGOUT.equals(text[0])) logout(key);
			messageQueue.add(new TalkServerJob(text[0],text[1],key));
		}
	}
	
	/** 이 메소드는 사용의 커넥션이 끊어졌을 경우 발생한다. */
	@Override
	protected void handleExpectedIOException(SelectionKey key) throws IOException{
		String exitId = (String)key.attachment();
		//log.debug("{0} 사용자와의 연결이 강제로 끊어졌습니다.",exitId);
		logout(key);
		messageQueue.add(new TalkServerJob(Protocol.EXIT,exitId,key));
	}
	
	private synchronized void logout(SelectionKey key) throws IOException {
		boolean keyExist = logined.remove(key);
		SocketChannel channel = (SocketChannel)key.channel(); 
		if(channel.isOpen()) channel.close();
		//if(keyExist && logined.size()==0) log.info("== 모든 사용자가 logout되었습니다. ==");
	}

	@Override
	public void shutdown() {
		super.shutdown();
		threadPool.shutdown();
	}

}
