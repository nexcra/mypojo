package erwins.util.lib;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ConcurrentModificationException;

public class ChannelUtils{
	
	/** 다중 접속 전파 중에 연결이 끊어질 수 있다. 이럴경우 무시한다.
	 * Exception을 처리하여 사용자를 map에서삭제하기도 전에 casting이 일어날때 발생된다.
	 * 나머지 예외는?? 몰라잉~
	 * flip()은 따로 해주어야 한다. */
	public static void broadcast(ByteBuffer whiteBuffer,Iterable<SelectionKey> collection){
		for(SelectionKey each : collection){
			SocketChannel channel = (SocketChannel) each.channel();
			try {
				channel.write(whiteBuffer);
			} catch (ClosedChannelException e) {  //NON
			} catch (IOException e) {
				//log.debug(e.getMessage() + " : " + e.getClass().getSimpleName());
			} catch (ConcurrentModificationException e) {
				//log.debug(e.getMessage() + " ?? " + e.getClass().getSimpleName());
			}
			whiteBuffer.rewind();
		}
	}
	
}