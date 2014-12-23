package erwins.util.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/** 
 * vo를 동적으로 받은 후 body를 출력할지 안할지 결정하도록 사용하자.
 *  */
@SuppressWarnings("serial")
public abstract class AbstractBodyIncludeTag extends TagSupport {
	
	protected abstract boolean isBodyInclude();

    @Override
    public int doStartTag() throws JspException {
        return isBodyInclude() ? TagSupport.EVAL_BODY_INCLUDE :  TagSupport.SKIP_BODY;
    }
	
}
