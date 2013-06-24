package erwins.jsample.domain.talk.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import erwins.jsample.domain.talk.BroadcastMessage;
import erwins.jsample.domain.talk.Protocol;
import erwins.util.lib.ChannelUtils;
import erwins.util.lib.CharEncodeUtil;

/** 성능을 위해 메세지를 받아 스래드로 처리한다.
 * 중요! 각 인스턴스당 하나의 whiteBuffer를 가진다.  */
public class TalkServerThread implements Runnable {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private final Set<SelectionKey> logined;
	private final BlockingQueue<TalkServerJob> messageQueue;
	private final Charset charset = CharEncodeUtil.C_UTF_8;
	
	public TalkServerThread(Set<SelectionKey> logined,BlockingQueue<TalkServerJob> messageQueue) {
		this.logined = logined;
		this.messageQueue = messageQueue;
	}

	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()){
				TalkServerJob job = messageQueue.take();
				eachProcess(job.getHeader(), job.getMessage(), job.getKey());
			}
		} catch (InterruptedException e1) {
			//아무것도 하지 않는다 
		}
	}

	private void eachProcess(String header,String text,SelectionKey key){
		//log.debug("{0} [{1}/{2}] : {3}",Thread.currentThread().getId(),key.attachment(),header,text);
		if(header.equals(Protocol.MESSAGE)){
			String senderId = (String) key.attachment();
			BroadcastMessage message = new BroadcastMessage();
			message.setSenderId(senderId);
			message.setMessage(text);
			messageBroadcast(Protocol.MESSAGE,message.toText());
		}else if(header.equals(Protocol.LOGIN)){
			login(text, key);
		}else if(header.equals(Protocol.LOGOUT) || header.equals(Protocol.EXIT)){
			messageBroadcast(header,text);
			loginInfoBroadcast();
		}else{
			messageReturn(key, Protocol.ERROR,header + text);
		}
	}

	/** text가 ID값이다. */
	private synchronized void login(String text, SelectionKey key){
		if(logined.contains(key)){
			String existId = (String)key.attachment();
			messageReturn(key,Protocol.ERROR_LOGIN_EXIST,existId,text);
		}else{
			key.attach(text);
			logined.add(key);
			messageBroadcast(Protocol.LOGIN,text);
			loginInfoBroadcast();
		}
	}

	private void loginInfoBroadcast() {
		String[] texts;
		synchronized (logined) { //ㅋㅋ 걍 뽀대
			texts = new String[logined.size()];
			int index = 0;
			Iterator<SelectionKey> i = logined.iterator();
			while(i.hasNext()){
				SelectionKey each = i.next();
				texts[index++] = (String)each.attachment();
			}
		}
		messageBroadcast(Protocol.LOGIN_INFO,texts);
	}
	

	
	/* ================================================================================== */
	/*                                 WRITE                                                   */
	/* ================================================================================== */
	
	private final ByteBuffer whiteBuffer = ByteBuffer.allocateDirect(1024); //일단 한개만
	

	private void input(String string) {
		whiteBuffer.clear();
		whiteBuffer.put(string.getBytes(charset));
		whiteBuffer.flip();
	}

	private void messageBroadcast(String header,String ... texts){
		buildMessage(header, texts);
		ChannelUtils.broadcast(whiteBuffer, logined);
	}
	
	private void messageReturn(SelectionKey key,String header,String ... texts){
		buildMessage(header, texts);
		try {
			((SocketChannel)key.channel()).write(whiteBuffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void buildMessage(String header, String... texts) {
		String input = Protocol.newMessage(header, texts);
		input(input);
	}

}
