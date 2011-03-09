
package erwins.util.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.springframework.mock.web.MockHttpServletResponse;

import erwins.util.lib.CharSets;
import erwins.util.lib.Encoders;
import erwins.util.morph.JDissolver;

/** json과 Http에 관한 유틸 */
public class JsonWriter {
	
	public static final String CONTENT_TYPE_XML = "text/xml; charset=" + CharSets.UTF_8;
	public static final String CONTENT_TYPE_JSON = "application/json; charset=" + CharSets.UTF_8;

    /** 안씀. */
    //public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    public static final String IS_SUCCESS = "isSuccess";
    public static final String MESSAGE = "message";
    
    public JsonWriter(HttpServletResponse resp){
    	this.resp = resp;
    }
    
    private boolean success = true;
    private final HttpServletResponse resp;
    private JSONObject json = new JSONObject();

    /**
     * success를 구분하는 json형태로 리턴한다. 
     * Flex의 경우 json타입이라 할지라도 XML구문이 들어오면 파싱 실패 오류를 낸다. 따라서 단순 문자열일경우 escape해 준다.
     * 자바스크립트의 경우 escape를 하면 그대로.. 출력된다.
     */
    public void write(Object obj) {
        PrintWriter out = getOut();
        json.put(IS_SUCCESS, success); //자바스크립트 isSuccess와 매칭된다.
        buildMessage(obj, json);
        out.write(json.toString());
    }

	private void buildMessage(Object obj, JSONObject json) {
		if (obj instanceof String) json.put(MESSAGE, Encoders.escapeFlex(obj.toString()));
        else if(obj instanceof JSON) json.put(MESSAGE, obj);
        else {
            JSON array = JDissolver.instance().build(obj);
            json.put(MESSAGE, array);
        }
	}

    /**
     * HttpServletResponse의 예외를 래핑한다.
     */
    public PrintWriter getOut() {
        resp.setContentType(CONTENT_TYPE_JSON);
        PrintWriter out;
        try {
            out = resp.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
    public void write(String message,Object ... obj) {
        write(MessageFormat.format(message, obj));
    }
    public JsonWriter setSuccess(boolean success) {
		this.success = success;
		return this;
	}
    public JsonWriter addHeader(String key,Object value) {
    	json.put(key, value);
    	return this;
    }

	/* ================================================================================== */
	/*                                 STATIC                                             */
	/* ================================================================================== */
    /** Mock 테스트시 json을 파싱해서 성공인지 확인한다. */
    public static JSON assertResponse(MockHttpServletResponse resp){
    	String body;
		try {
			body = resp.getContentAsString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
        JSONObject json = JSONObject.fromObject(body);
        String message = json.getString(JsonWriter.MESSAGE);
        if(!json.getBoolean(JsonWriter.IS_SUCCESS)) throw new RuntimeException(message.toString());
        if(message.startsWith("{") && message.endsWith("}")) return  json.getJSONObject(JsonWriter.MESSAGE);
        else if(message.startsWith("[") && message.endsWith("]")) return  json.getJSONArray(JsonWriter.MESSAGE);
        return null;
    }    

}
