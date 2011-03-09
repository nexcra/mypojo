package erwins.domain.talk;


public class BroadcastMessage implements TalkMessage{
	
	private String senderId;
	private String message;
	
	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String sender) {
		this.senderId = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void parsText(String text) {
		String[] each = Protocol.splitText(text);
		senderId = each[0];
		message = each[1];
	}

	@Override
	public String toText() {
		return Protocol.mergeText(senderId,message);
	}
	
	
	
}