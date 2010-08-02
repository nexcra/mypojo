package erwins.util.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import erwins.util.lib.Strings;

/** 이 스래드는 1개만 기동한다고 가정한다. */
public class MessageExecutor implements Runnable {
	
	private final Selector selector;
	private final MessageCallback messageCallback;
	private CharsetUtils util = new CharsetUtils();
	private ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	
	
	public MessageExecutor(Selector selector,MessageCallback messageCallback){
		this.selector  = selector;
		this.messageCallback = messageCallback;
	}
	
	private static final int BUFFER_SIZE = 1024;

	/** 클라이언트는 하나의 서버에서만 데이터를 가져옴으로 SocketChannel이 항상 고정적이다. */
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				ByteBuffer temp = null;
				while (it.hasNext()) {
					SelectionKey key = it.next();
					if (key.isReadable()) readBuffer(temp, key);
					it.remove();
				}
			} catch (ClosedChannelException e){
				//닫아버린 키를 읽으려 할때 / 인터럽트 할때 생긴다. 걍 무시한다.1
			} catch (ClosedSelectorException e){
				//닫아버린 키를 읽으려 할때 생긴다. 걍 무시한다.
			} catch (CancelledKeyException e){
				//취소한 키를 읽으려 할때 생긴다. 무시한다. 
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		//log.debug("Display Thread를 종료합니다.");
	}

	/** 데이터가 길어서 2번 이상 읽어야 할 수도 있다. 연속해 들어온다고 일단 가정한다.
	 * 데이터가 버퍼 사이즈를 넘을 경우 임시 버퍼를 만들어 사용하고 삭제해 준다.  */
	private void readBuffer(ByteBuffer temp, SelectionKey key) throws IOException {
		SocketChannel server = (SocketChannel) key.channel();
		readBuffer.clear();
		int readed = server.read(readBuffer);
		
		if(readed == BUFFER_SIZE){
			temp = ByteBuffer.allocate(BUFFER_SIZE * 10);
			temp.put(readBuffer);
		}else{
			if(temp==null){
				readBuffer.flip();
				String returned = util.decode(readBuffer);
				readServerMessage(returned);	
			}else{
				temp.put(readBuffer);
				temp.flip();
				String returned = util.decode(temp);
				readServerMessage(returned);
				temp = null;
			}
		}
	}

	/** 종료시 ""같은 의미없응 데이터가 들오온다? 암튼 스킵 */
	private void readServerMessage(String returned) {
		if(Strings.isEmpty(returned)) return;
		messageCallback.messageFromServer(returned);
	}
	
}
