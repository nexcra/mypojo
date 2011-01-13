package erwins.util.vender.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.context.ContextLoader;

import erwins.util.lib.ReflectionUtil;

@SuppressWarnings("rawtypes")
public class Springs {
	
	/** jdbc의 result맵을 Map으로 변형해준다. 리턴값은 가능하다면 apache의 ListOrderedMap를 리턴한다. */
	public static RowMapper RESULTMAP_TO_MAP = new ColumnMapRowMapper();
	
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
	
	/** AOP를 사용할때 aop가 걸린 메소드의 annotation을 가져온다. */
	public static <T extends Annotation> T getAnnotaion(JoinPoint joinPoint,Class<T> annoClazz) {
		Class clazz = joinPoint.getTarget().getClass();
		Method m = ReflectionUtil.getMethodByName(clazz, joinPoint.getSignature().getName(), joinPoint.getArgs().length);
		return m.getAnnotation(annoClazz);
	}
}
