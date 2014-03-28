package erwins.util.web.jsp;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Hidden파라메터를 입력해준다.
 * @author sin 
 */
@SuppressWarnings("serial")
public class HiddenTag extends TagSupport {
	
	private Logger log = LoggerFactory.getLogger(getClass());
    
    /** 히든값을 세팅해준다. 이후 jsp에서 태그를 호출해야 한다.
     * 나중에 VO로 변경할것! */
    //@SuppressWarnings("unchecked")
    public static void setHiddenValue(HttpServletRequest req,String ... args){
    	/*
        Map<String,String[] > hiddenValue = new HashMap<String, String[] >();
        if(args.length==0){
            List<String> names = OcUtil.enumerationToList(req.getParameterNames());
        	List<String> names = null;
            args = names.toArray(new String[names.size()]);
        }
        for(String each : args) hiddenValue.put(each, req.getParameterValues(each));
        req.setAttribute(HIDDEN_KEY, hiddenValue);
        */
    }
    
    private static final String HIDDEN_KEY = "hiddenTag";
    
    private String value = HIDDEN_KEY;

    @Override
    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException {
        Map<String,String[]> hiddenValue = (Map<String, String[]>) pageContext.getRequest().getAttribute(value);
        JspWriter out = pageContext.getOut();
        for(Entry<String,String[]> entry : hiddenValue.entrySet()){
            try {
                String[] values = entry.getValue();
                if(values==null) continue;
                for(String value : values)
                    out.println("<input type='hidden' name='"+entry.getKey()+"' value='"+value+"' />"+IOUtils.LINE_SEPARATOR_WINDOWS);
            } catch (IOException e) {
                log.error("HiddenTag.doStartTag() Error!", e);
            }
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
