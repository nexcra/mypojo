package erwins.util.spring.view;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import erwins.util.web.WebUtil;

/** 
 * AbstractJsonFactory 를 위해서 새로 만듬.
 * 
 * 추가하고싶은것들을 확장해서 사용
 * Gson은 json으로 변환시에는 중첩 컬렉션 객체를 지원하지만 그 역변환은 허용되지 않는다.
 *   */
public class GsonView implements View {
	
	private final Gson gson;
	private final String encoding;
	private final String contentType;
	private final String successKey;
	private final String messageKey;
	
	public GsonView(Gson gson,String encoding, String contentType, String successKey, String messageKey) {
		super();
		this.gson = gson;
		this.encoding = encoding;
		this.contentType = contentType;
		this.successKey = successKey;
		this.messageKey = messageKey;
	}

	private JsonObject body = new JsonObject();
	private JsonElement message;
    private boolean success = true;
    /** true일 경우 한번 적용된다. */
    private boolean flat = false;
    
    /** 직접 body에 입력할때 한해서 사용한다. 특수용도 */
    public GsonView setObjectToBody(String key, Object obj) {
    	body.add(key, toJson(obj));
		return this;
	}
    
    /** 기존 메세지가 있어도 오버라이트 */
    public GsonView msg(String msg,Object ... obj) {
    	if(obj.length != 0 ) msg =  MessageFormat.format(msg, obj);
    	message = new JsonPrimitive(msg);
        return this;
    }
    
    public GsonView of(Object obj) {
    	message = toJson(obj);
        return this;
    }
    
    /** flat 지정후 바로 toJson()이 호출되어야 한다. */
    public GsonView flat(boolean flat) {
		this.flat = flat;
		return this;
	}

	/** 동적으로 추가할때 적용된다. 한번이라도 호출한다면 setMessage는 무시된다. */
    public GsonView add(String key, Object obj) {
    	if(message==null) message = new JsonObject();
    	if(!JsonObject.class.isAssignableFrom(message.getClass())) message = new JsonObject();
    	JsonObject msgJson = (JsonObject)message;
    	msgJson.add(key, toJson(obj));
    	return this;
    }
    
    public JsonElement toJson(Object obj) {
    	if(obj instanceof String) return new  JsonPrimitive((String)obj);
    	if(obj instanceof Number) return new  JsonPrimitive((Number)obj);
    	if(obj instanceof Boolean) return new  JsonPrimitive((Boolean)obj);
    	if(obj instanceof JsonElement) {
    		JsonElement json = (JsonElement)obj;
    		if(flat) {
    			flat = false;
    			return toFlatData(json);
    		}
    		return json;
    	}
    	else{
    		JsonElement json = gson.toJsonTree(obj);
    		if(flat) {
    			flat = false;
    			return toFlatData(json);
    		}
    		return json;
    	}
    }
    
    public GsonView isFail() {
		this.success = false;
		return this;
	}
    
    public GsonView setSuccess(boolean isSuccess) {
  		this.success = isSuccess;
  		return this;
  	}

	/** resp를 닫아주지 않는다. 누가 해주겠지. */
	@Override
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse resp) throws Exception {
		render(resp);
	}
	
	/** 외부에서 호출할때 사용하자 */
	public void render(HttpServletResponse resp) throws IOException {
		//resp.setHeader("Pragma", "private");
		//resp.setHeader("Cache-Control", "private, must-revalidate");
		resp.setContentType(getContentType()+"; charset=" + encoding);
		WebUtil.cacheForSeconds(resp, -1, true);
		PrintWriter writer =  resp.getWriter();
		JsonObject jsonBody = getBody();
        writer.write(jsonBody.toString());
	}
	
	public JsonObject getBody(){
		body.addProperty(successKey, success);
		if(message!=null)  body.add(messageKey, getMessage());
		return body;
	}
	
	public JsonElement getMessage(){
		return message;
	}
	
	public JsonArray getMessageArray(){
		return getMessage().getAsJsonArray();
	}
	
	public JsonObject getMessageObject(){
		return getMessage().getAsJsonObject();
	}
	
	/** 
	 * 객체 안에 객체가 들어있는 tree 구조의 자료를 일자 구조로 만들어준다.
	 * 여기서 key는 . 으로 연결된다.  
	 * ext 등의 flat만 지원하는 그리드에서 사용한다. */
	private JsonElement toFlatData(JsonElement message) {
		if(message.isJsonObject()) return toFlatData(message.getAsJsonObject());
		else if(message.isJsonArray()) return toFlatData(message.getAsJsonArray());
		else return message;
	}
	
	private JsonElement toFlatData(JsonArray msg) {
		JsonArray newJson = new JsonArray();
		for(JsonElement each : msg){
			newJson.add(toFlatData(each));
		}
		return newJson;
	}
	
	private JsonElement toFlatData(JsonObject msg) {
		JsonObject newJson = new JsonObject();
		for(Entry<String, JsonElement> entry : msg.entrySet()){
			JsonElement value = entry.getValue();
			if(value.isJsonPrimitive()) newJson.add(entry.getKey(), value);
			else if(value.isJsonObject()){
				JsonObject subMsg = (JsonObject)value;
				for(Entry<String, JsonElement> subEntry : subMsg.entrySet()){
					newJson.add(entry.getKey() + "." + subEntry.getKey(), subEntry.getValue());	
				}
			}
		}
		return newJson;
	}

	@Override
	public String getContentType() {
		return contentType;
	}
	
}