package erwins.util.web.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * boolean 표현식을 문자로 바꿔준다.
 * 누가 만든 태그가 있을거 같은데~~
 * @author sin 
 */
@SuppressWarnings("serial")
public class BooleanTag extends TagSupport {

	private Logger log = LoggerFactory.getLogger(getClass());

    private Object value;
    private String y;
    private String n;

    @Override
    public int doStartTag() throws JspException {
        if(value==null) throw new JspException("value는 null이 될 수 없습니다. ");
        Boolean yn = null;
        if(value instanceof Number){
            Number v = (Number)value;
            if(v==null) yn = Boolean.FALSE;
            else yn = v.intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;
        }else if(value instanceof String){
            //~~
        }
        if(yn==null) throw new JspException("처리할 수 없은 표현식입니다. : " + value);
        JspWriter out = pageContext.getOut();
        try {
            out.println(yn ? y : n);
        } catch (IOException e) {
        	log.error("BooleanTag.doStartTag() Error!", e);
        }
        y = null;
        n = null;
        value = null;
        return TagSupport.SKIP_BODY;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
    
    
    
    
	
	
	
	
}
