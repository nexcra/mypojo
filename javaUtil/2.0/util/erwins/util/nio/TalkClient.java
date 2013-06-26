package erwins.util.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import erwins.util.text.CharEncodeUtil;

public class TalkClient {
	
	protected final Selector selector;
	protected final SocketChannel channel;
	private final Charset charset = CharEncodeUtil.C_UTF_8;

	public TalkClient(String adress,int port){
		try {
			selector = Selector.open();
			channel = SocketChannel.open(new InetSocketAddress(adress, port));
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	protected Thread executeThread;
	
	private final ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1024);
	
	/** 나중에 콜백을 여러개? 추가할 수 있게 변경하자. */
	public void messageStartup(MessageCallback callBack) {
		MessageExecutor exe = new MessageExecutor(selector,callBack);
		executeThread = new Thread(exe);
		executeThread.setName("TalkClient_ExecuteThread");
		executeThread.start();
	}
	
	/** 안드로이드의 경우 콜백을 임의로 변경해야 해서 메소드를 추가했다. */
	public void messageShutdown() {
		executeThread.interrupt();
		executeThread = null;
	}

	/** 1.6 이상의 환경에서만 동작한다. */
	public void sendToServer(String message){
		sendToServer(message.getBytes(charset));
	}
	
	public void sendToServer(byte[] data){
		inputBuffer.clear();
		inputBuffer.put(data);
		inputBuffer.flip();
		try {
			channel.write(inputBuffer);
		} catch (IOException e) {
			//channel.close(); ///?????
			throw new RuntimeException(e);
		}
	}
	
	/** close()를 해야하는지는 모르겠당~ ㅋㅋ */
	public void shutdown(){
		executeThread.interrupt();
		try {
			channel.close();
			selector.close();
		} catch (IOException e) { //NON
		}
	}
}
