package erwins.util.vender.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

public class Springs {
	
	public static ApplicationContext getApplicationContext() {
		return ContextLoader.getCurrentWebApplicationContext();
	}
	
	public static boolean containsBean(String beanName) {
		return getApplicationContext().containsBean(beanName);
	}
	
	/**
	 * bean을 가져오자. reflection할거 아니면 거의 쓸일은 없을듯.
	 */
	public static Object getBean(String beanName) {
		return getApplicationContext().getBean(beanName);
	}
}
