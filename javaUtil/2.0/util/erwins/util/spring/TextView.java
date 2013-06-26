package erwins.util.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import erwins.util.web.WebUtil;

/** Ext에서 file upload하면 json리턴이 안된다.. 이때 사용 */
public class TextView implements View {

	private static final String CONTENT_TYPE =  "text/html";
	
	private String encoding = "UTF-8";
	private String text;
	
	public TextView(String text){
		this.text = text;
	}
	
	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}
	/** resp를 닫아주지 않는다. 누가 해주겠지. */
	@Override
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse resp) throws Exception {
		render(resp);
	}
	
	/** 외부에서 호출할때 사용하자 */
	public void render(HttpServletResponse resp) throws IOException {
		resp.setContentType(getContentType()+"; charset=" + encoding);
		WebUtil.cacheForSeconds(resp, -1, true);
		PrintWriter writer =  resp.getWriter();
        writer.write(text);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	

}
