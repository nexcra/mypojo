package erwins.util.spring.web;

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

import erwins.util.spring.view.GsonView;
import erwins.util.web.WebUtil;

/** 추가하고싶은것들을 확장해서 사용
 * Gson은 json으로 변환시에는 중첩 컬렉션 객체를 지원하지만 그 역변환은 허용되지 않는다.
 * TypeToken을 잘 활용해야 한다.
 * 
 *   이제 사용 금지~
 *   @Deprecated 는 아직 안붙임
 *   @see GsonView
 *   
 *   */
public abstract class AbstractGsonView implements View {

	private static final String UTF_8 = "UTF-8";
	//private static final String CONTENT_TYPE =  "application/json";
	/**  IE에서 application/json로 보내면 다운로드가 되는 현상이 있다. 아래로 변경해준다 */
	private static final String CONTENT_TYPE =  "text/html";
	private static final String IS_SUCCESS_KEY = "success";
	private static final String MESSAGE_KEY = "message";
	
	protected abstract Gson getGson();
	
    protected JsonObject body = new JsonObject();
    protected JsonElement message;
    private boolean success = true;
    /** true일 경우 한번 적용된다. */
    private boolean flat = false;
    
    public AbstractGsonView(){}
    
    public AbstractGsonView(Object obj){
    	message = toJson(obj);
    }
    
    /** 직접 body에 입력할때 한해서 사용한다. */
    public AbstractGsonView setObject(String key, Object obj) {
    	body.add(key, toJson(obj));
		return this;
	}
    
    /** 기존 메세지가 있어도 오버라이트 */
    public AbstractGsonView setMessageStr(String msg,Object ... obj) {
    	if(obj.length != 0 ) msg =  MessageFormat.format(msg, obj);
    	message = new JsonPrimitive(msg);
        return this;
    }
    
    public AbstractGsonView setMessage(Object obj) {
    	message = toJson(obj);
        return this;
    }
    
    public AbstractGsonView setFlat(boolean flat) {
		this.flat = flat;
		return this;
	}

	/** 동적으로 추가할때 적용된다. 한번이라도 호출한다면 setMessage는 무시된다. */
    public AbstractGsonView addMessage(String key, Object obj) {
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
    		JsonElement json = getGson().toJsonTree(obj);
    		if(flat) {
    			flat = false;
    			return toFlatData(json);
    		}
    		return json;
    	}
    }
    
    public AbstractGsonView isFail() {
		this.success = false;
		return this;
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}
	/** 필요하다면 오버라이딩 */
	public String getSuccesskey() {
		return IS_SUCCESS_KEY;
	}
	/** 필요하다면 오버라이딩 */
	public String getMessagekey() {
		return MESSAGE_KEY;
	}
	public String getEncoding() {
		return UTF_8;
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
		resp.setContentType(getContentType()+"; charset=" + getEncoding());
		WebUtil.cacheForSeconds(resp, -1, true);
		PrintWriter writer =  resp.getWriter();
		JsonObject jsonBody = getBody();
        writer.write(jsonBody.toString());
	}
	
	public JsonObject getBody(){
		body.addProperty(getSuccesskey(), success);
		if(message!=null)  body.add(getMessagekey(), getMessage());
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
	
}
