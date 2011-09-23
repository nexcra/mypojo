package erwins.webapp.myApp;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import erwins.util.web.WebUtil;

public class AjaxTextView implements View{
	
	private final String text;
	
	public AjaxTextView(String text){
		this.text = text;
	}
	
	@Override
	public String getContentType() {
		return "text/html";
	}
	@Override
	public void render(Map<String, ?> arg0, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setContentType(getContentType());
		WebUtil.cacheForSeconds(resp, -1, true);
		PrintWriter writer =  resp.getWriter();
        writer.write(text);
	}

}
