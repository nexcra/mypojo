
package erwins.util.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractResponseWriter{
	
	
	protected ResponseOutConfig rule;
	private String contentTypeString;
	
	/** 반드시 호출해주어야 한다. */
	public void init(ResponseOutConfig rule){
		this.rule = rule;
		this.contentTypeString = rule.getContentType().getValue()+"; charset=" + rule.getEncode();
	}
	
	public PrintWriter getWriter(HttpServletResponse resp){
		resp.setContentType(contentTypeString);
		if(!rule.isCache()) WebUtil.cacheForSeconds(resp, -1, true);
        try {
			return resp.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
