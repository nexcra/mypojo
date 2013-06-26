package erwins.util.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import erwins.util.text.CharEncodeUtil;
import erwins.util.validation.InputValidationException;


/** 나중에 소스참고를 위해 범용적인 부분을 나눈다. 
 * 일단 성능은 생각하지 않고 String으로 구현한다. */
public abstract class TalkServerTemplate implements Shutdownable {
	
	private Selector selector;
	
	/** ServerSocketChannel은 OP_ACCEPT만 지원한다. */
	public TalkServerTemplate(int port){
		try {
			selector = Selector.open();
			ServerSocketChannel server = ServerSocketChannel.open();
			server.configureBlocking(false);
			ServerSocket socker = server.socket();
			socker.bind(new InetSocketAddress(InetAddress.getLocalHost(),port));
			server.register(selector, SelectionKey.OP_ACCEPT); //
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Thread acceptor;

	/** logOut또는 연결 종료 등으로 close되었으나 입력값이 들어올 수 있다. 
	 * 이러한 예외는 무시해준다.*/
	public void startup(){
		acceptor = new Thread(new Runnable() {
			@Override
			public void run() {
				ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024); //일단 한개만
				int waitCount = 0;
				int ignore = 0;
				while(!Thread.currentThread().isInterrupted()){
					try {
						//log.debug("{0} : wait..",waitCount++);
						selector.select();
						Iterator<SelectionKey> it = selector.selectedKeys().iterator();
						while(it.hasNext()){
							SelectionKey key = it.next();
							if(key.isAcceptable()) accept(key);
							else if(key.isReadable()) readInput(readBuffer, key);
							else throw new RuntimeException("???");
							it.remove();
						}
					} catch (CancelledKeyException e) {
						//log.debug("{0} ignored input data",ignore);
					} catch (ClosedChannelException e) {
						throw new RuntimeException(e);
					} catch (IOException e) {
						throw new RuntimeException(e);
					} catch (InputValidationException e) {
						String message = null;
						try {
							message = CharEncodeUtil.C_UTF_8.decode(readBuffer).toString();
						} catch (Exception e1) { //무시한다.
						}
						//log.error("[{0}] : Malformed message from client", message);
					}
				}
			}
		});
		acceptor.setName("TalkServer_Acceptor");
		acceptor.start();
	}
	
	/** 메인 스래드를 닫아주자. */
	@Override
	public void shutdown() {
		acceptor.interrupt();
	}
	
	private void accept(SelectionKey key) throws IOException, ClosedChannelException {
		ServerSocketChannel ch =  (ServerSocketChannel)key.channel();
		SocketChannel client = ch.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
		//log.debug("new Accept registered");
	}	
	
	/** 여기서 예외가 나는것은 갑작스런 client의 종료이다. 나머지 예외는 나면 안된다. 
	 * 일정 바이트 이상의 데이터를 연속 읽기를 하도록 수정
	 * 중요: 클라이언트의 무단종료시 최초의 읽기인데도 -1을 읽는 경우가 있다. 이경우 cancel한다. */
	private void readInput(ByteBuffer buffer, SelectionKey key){
		buffer.clear();
		SocketChannel client =  (SocketChannel)key.channel();
		try {
			int read = client.read(buffer);
			if(read==-1){
				handleExpectedIOException(key);
				return;
			}
			buffer.flip();
			String returned = CharEncodeUtil.C_UTF_8.decode(buffer).toString();
			inputFromClient(key,returned);
		} catch (IOException e) { 
			try {
				handleExpectedIOException(key);
				//client.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
	}
	
	protected abstract void inputFromClient(SelectionKey key,String message) throws IOException ;
	
	protected abstract void handleExpectedIOException(SelectionKey key) throws IOException;
	
}
