package erwins.jsample.domain.talk.server;

import java.nio.channels.SelectionKey;


public class TalkServerJob{
	
	private final String header;
	private final String message;
	private final SelectionKey key;
	
	public TalkServerJob(String header,String message,SelectionKey key){
		this.header = header;
		this.message = message;
		this.key = key;
	}
	
	public String getHeader() {
		return header;
	}

	public String getMessage() {
		return message;
	}

	public SelectionKey getKey() {
		return key;
	}

}
