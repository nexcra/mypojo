package erwins.util.vender.spring;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.springframework.web.servlet.View;

import erwins.util.morph.BeanToJson;
import erwins.util.web.ResponseOutConfig;
import erwins.util.web.WebUtil;

@Deprecated
public class AbstractAjaxView implements View{
	
	private final BeanToJson beanToJson;
	private final ResponseOutConfig config;
	
	
	public AbstractAjaxView(BeanToJson beanToJson, ResponseOutConfig config) {
		this.beanToJson = beanToJson;
		this.config = config;
	}

	protected boolean success = true;
    protected JSONObject json = new JSONObject();

    public AbstractAjaxView add(Object obj) {
    	addEntity(config.getMessageKey(),obj);
        return this;
    }
    
    public AbstractAjaxView add(String message,Object ... obj) {
    	addEntity(config.getMessageKey(),MessageFormat.format(message, obj));
        return this;
    }

    /** 직접 입력할때 한해서 사용한다. */
    public AbstractAjaxView addObject(String key, Object obj) {
    	json.put(key, obj);
		return this;
	}
    public AbstractAjaxView addEntity(String key, Object obj) {
    	if (obj instanceof String || obj instanceof Number || obj instanceof JSON) json.put(key, obj);
    	else{
    		JSON array = beanToJson.build(obj);
    		json.put(key, array);
    	}
    	return this;
    }
    
    public AbstractAjaxView isFail() {
		this.success = false;
		return this;
	}

	@Override
	public String getContentType() {
		return config.getContentTypeString();
	}

	/** resp를 닫아주지 않는다. 누가 해주겠지. */
	@Override
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse resp) throws Exception {
		json.put(config.getIsSuccessKey(), success);
		if(config.isXmlEscape()) BeanToJson.escapeForFlex(json);
		
		resp.setContentType(getContentType());
		if(!config.isCache()) WebUtil.cacheForSeconds(resp, -1, true);
		PrintWriter writer =  resp.getWriter();
        writer.write(json.toString());
	}

}
