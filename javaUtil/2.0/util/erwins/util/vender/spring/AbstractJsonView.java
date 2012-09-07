package erwins.util.vender.spring;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.springframework.web.servlet.View;

import erwins.util.web.WebUtil;

/** 추가하고싶은것들을 확장해서 사용 */
public abstract class AbstractJsonView implements View {

	private static final String UTF_8 = "UTF-8";
	private static final String CONTENT_TYPE =  "application/json";
	private static final String IS_SUCCESS_KEY = "success";
	private static final String MESSAGE_KEY = "message";
	
	/** 편의상 어쩔 수 없이 스태택.. ㅠ 
	 * JsonConfig를 사용하지 않으면 일반 리플렉션 버전보다 훨씬 느리다. */
	protected static JsonConfig JSON_CONFIG = new JsonConfig();
	
    protected JSONObject body = new JSONObject();
    protected JSONObject message;
    private boolean success = true;
    
    public AbstractJsonView setMessage(String message,Object ... obj) {
    	body.put(getMessagekey(), MessageFormat.format(message, obj));
        return this;
    }
    public AbstractJsonView setMessage(Object obj) {
    	addToJson(body,getMessagekey(),obj);
        return this;
    }

    /** 직접 body에 입력할때 한해서 사용한다. */
    public AbstractJsonView setObject(String key, Object obj) {
    	addToJson(body,key,obj);
		return this;
	}
    /** 동적으로 추가할때 적용된다. 한번이라도 호출한다면 setMessage는 무시된다. */
    public AbstractJsonView addMessage(String key, Object obj) {
    	if(message==null) message = new JSONObject();
    	addToJson(message,key,obj);
    	return this;
    }
    
    private static void addToJson(JSONObject json,String key,Object obj) {
    	if (obj instanceof String || obj instanceof Number || obj instanceof JSON) json.put(key, obj);
    	else{
    		JSON array = JSONSerializer.toJSON(obj,JSON_CONFIG);
    		json.put(key, array);
    	}
    }
    
    public AbstractJsonView isFail() {
		this.success = false;
		return this;
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}
	public String getSuccesskey() {
		return IS_SUCCESS_KEY;
	}
	public String getMessagekey() {
		return MESSAGE_KEY;
	}
	public String getEncoding() {
		return UTF_8;
	}
	/** resp를 닫아주지 않는다. 누가 해주겠지. */
	@Override
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse resp) throws Exception {
		body.put(getSuccesskey(), success);
		if(message!=null)  body.put(getMessagekey(), message);
		resp.setContentType(getContentType()+"; charset=" + getEncoding());
		WebUtil.cacheForSeconds(resp, -1, true);
		PrintWriter writer =  resp.getWriter();
        writer.write(body.toString());
	}

}
