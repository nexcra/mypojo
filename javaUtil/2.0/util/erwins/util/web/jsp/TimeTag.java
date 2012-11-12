package erwins.util.web.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 문자열 time형식을 아래와 같이 바꿔준다.
 * 2012/12/31 12:34:56
 * @author sin 
 */
@SuppressWarnings("serial")
public class TimeTag extends TagSupport {

	private Logger log = LoggerFactory.getLogger(getClass());
    
    private String value;

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            //out.println(OcUtil.toTimeFormat3(value));~~
        	out.println("");
        } catch (IOException e) {
        	log.error("HiddenTag.doStartTag() Error!", e);
        }
        return TagSupport.SKIP_BODY;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
	
	
	
	
}
