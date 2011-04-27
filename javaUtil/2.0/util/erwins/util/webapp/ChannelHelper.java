package erwins.util.webapp;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.repackaged.com.google.common.util.Base64;

import erwins.util.lib.StringUtil;

public abstract class ChannelHelper{

	private final Map<String, String> map = new HashMap<String, String>();

	/** message에 들어가는 내용에 한글이 포함되면 안된다. 
	 *  귀찮으니 통째로 Base64인코드 해준다.
	 */
	public void sendJson(String id, JSON json) {
		try {
			sendMessage(id,Base64.encode(json.toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** 자기 빼고 나머지한테 모드 메세지 전송. */
	public void broadcastJson(String id, JSON json) {
		for(Entry<String,String> entry : map.entrySet()){
			if(entry.getKey().equals(id)) continue;
			sendJson(entry.getKey(), json);
		}
	}
	public void broadcastMessage(String id, String message) {
		for(Entry<String,String> entry : map.entrySet()){
			if(entry.getKey().equals(id)) continue;
			sendMessage(entry.getKey(), message);
		}
	}

	public void sendMessage(String id, String message) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelMessage channelMessage = new ChannelMessage(id,message);
		channelService.sendMessage(channelMessage);
	}
	
	/** ID당 1개의 토큰(연결)을 지원한다. (페이지당 1개가 아니다)
	 * 따라서 동일한 유저가 2개의 탭에 페이지를 띄우면 각 페이지마다 메세지를 받게 된다. */
	public String getOrCreateToken(String id) {
		String token = map.get(id);
		if(token==null){
			ChannelService channelService = ChannelServiceFactory.getChannelService();
		    token = channelService.createChannel(id);
		    map.put(id, token);			
		}
	    return token;
	}
	
	/** 해당 키를 리셋한다. 첫 로그인 후 두번째 로그인때 세션이 틀려지면 오류가 나는듯? 그걸 방지 */
	public void remove(String key) {
		map.remove(key);
	}
	public int size() {
		return map.size();
	}
	
	protected abstract String getMessageKey();
	
	public void sendSimpleMessage(String id, String message) {
		JSONObject json = convertToJson(message);
		sendJson(id,json);
	}
	public void broadcastSimpleMessage(String id, String message,Object ... params) {
		JSONObject json = convertToJson(StringUtil.format(message, params));
		broadcastJson(id,json);
	}
	private JSONObject convertToJson(String message) {
		JSONObject  json = new JSONObject();
		json.put(getMessageKey(), message);
		return json;
	}
	

}
