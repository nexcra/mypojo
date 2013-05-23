package erwins.util.vender.spring;

import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import erwins.util.lib.StringUtil;


/**
 * 스프링 bean을 가져오는 코드 추가
 * @author sin 
 */
@SuppressWarnings("serial")
public class SpringTagSupport extends TagSupport {
	
	@SuppressWarnings("unchecked")
	protected <T> T getBean(String beanName){
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
    	T tree = (T) ctx.getBean(beanName);
    	return tree;
	}
	
	protected <T> T getBean(Class<T> clazz){
    	return getBean(StringUtil.uncapitalize(clazz.getSimpleName()));
	}
	
}
