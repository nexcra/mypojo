package erwins.util.vender.spring;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.web.context.ContextLoader;

import erwins.util.lib.ReflectionUtil;

/** 스프링용 유틸 모음 */
public abstract class SpringUtil {
	
	/** jdbc의 result맵을 Map으로 변형해준다. 리턴값은 가능하다면 apache의 ListOrderedMap를 리턴한다. */
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("rawtypes")
	public static <T extends Annotation> T getAnnotaion(JoinPoint joinPoint,Class<T> annoClazz) {
		Class clazz = joinPoint.getTarget().getClass();
		Method m = ReflectionUtil.getMethodByName(clazz, joinPoint.getSignature().getName(), joinPoint.getArgs().length);
		return m.getAnnotation(annoClazz);
	}
	
    /** 해당 url의 인터페이스로 RMI객체를 리턴한다.
     * 해당 URL의 WAS에는 스프링 RMI객체를 서비스할 수 있도록 설정이 되어있어야 한다. */
    @SuppressWarnings("unchecked")
    public static <T> T convertToRmiInstance(String url,Class<T> clazz){
        HttpInvokerProxyFactoryBean b = new HttpInvokerProxyFactoryBean();
        b.setServiceUrl(url);
        b.setServiceInterface(clazz);
        b.afterPropertiesSet();
        T rmi = (T) b.getObject();
        return rmi;
    }
    
    /** xml의 파라메터로 받는 리소스를 File로 변환한다.  IOException 제거 */
    public static List<File> toFiles(Resource[] mappingLocations) {
        List<File> locationSqlFiles = new ArrayList<File>();
        for(Resource each : mappingLocations){
            try {
                locationSqlFiles.add(each.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return locationSqlFiles;
    }
}
