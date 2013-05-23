package erwins.util.vender.spring;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import erwins.util.lib.CollectionUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.StringUtil;

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
    
    /** 간단 PS 제조기.
     *  ? 순서대로 매핑한다. */
    public static PreparedStatementSetter buildPreparedStatement(final Object ... params) {
        PreparedStatementSetter ps = new PreparedStatementSetter(){
            @Override
            public void setValues(PreparedStatement arg0) throws SQLException {
                for(int i=0;i<params.length;i++) arg0.setObject(i+1,params[i]); //1부터 시작한다.
            }
        };
        return ps;
    }
    
    /** url들을 모아볼때 사용한다. */
    public static Map<String,String> getHandlerMap(AbstractUrlHandlerMapping handlerMapping){
        Map<String,String> map =  new TreeMap<String, String>();
        for(Entry<String,Object> e : handlerMapping.getHandlerMap().entrySet()){
            String urlName = e.getKey();
            if(urlName.endsWith(".*") || urlName.endsWith("/")) continue; 
            String controllerName = StringUtil.getFirst(e.getValue().toString(), "@");
            map.put(urlName, controllerName);
        }
        return map;
    }
    
    public static class UrlMap{
        public String url;
        public Class<?> clazz;
        public Method method;
    }
    
    /** 빈 리소스를 리턴한다. */
    public static Resource getEmptyResource(){
        return new InputStreamResource(IOUtils.toInputStream(""));
    }
    
    /** 어노테이션 / 메소드 매핑으로 등록된 애들도 함께 보여준다 */
    public static List<UrlMap> getHandlerMapType(AbstractUrlHandlerMapping handlerMapping){
        Map<String,UrlMap> map =  new TreeMap<String, UrlMap>();
        
        for(Entry<String,Object> e : handlerMapping.getHandlerMap().entrySet()){
            String urlName = e.getKey();
            if(urlName.endsWith(".*") || urlName.endsWith("/")) continue;
            
            Object controller = e.getValue();
            String controllerName = StringUtil.getFirst(controller.toString(), "@");
            Class<?> clazz = ReflectionUtil.forName(controllerName);
            
            if(urlName.endsWith("*")){
                List<Method> methods =  ReflectionUtil.getMethodByAnnotation(clazz, RequestMapping.class);
                for(Method each : methods){
                    String subUrl = urlName.substring(0, urlName.length()-1);
                    UrlMap um = new UrlMap();
                    um.url = subUrl + each.getName();
                    um.method = each;
                    um.clazz = clazz;
                    map.put(um.url, um);
                }
            }else{
                String methodName = StringUtil.getLast(urlName, "/");
                Method method = ReflectionUtil.getMethodByName(clazz, methodName);
                if(method == null) method = findRequest(clazz, methodName);
                
                UrlMap um = new UrlMap();
                um.url = urlName;
                um.method = method;
                um.clazz = clazz;
                map.put(um.url, um);
            }

        }
        return CollectionUtil.toList(map.values());
    }
    
    /** 해당 methodName으로 매핑된 메소드를 찾아낸다. 
     * rest가 아닌 XXX.do 이런식의 매핑을 찾을때 사용한다. */
    @SuppressWarnings("rawtypes")
	private static Method findRequest(Class clazz, String methodName) {
        List<Method> methods =  ReflectionUtil.getMethodByAnnotation(clazz, RequestMapping.class);
        for(Method each : methods){
            RequestMapping rm = each.getAnnotation(RequestMapping.class);
            String[] annoParam = rm.value();
            for(String mappedUrl : annoParam) if(mappedUrl.endsWith(methodName)) return  each;
        }
        return null;
    }
    
    /**
     * 간단 바이트 수를 리턴한다. 256 이하면 1byte짜리 / 256이상이면 2byte짜리 오라클 UTF-8의 경우 3배 해야할듯
     */
    public static int getSimpleByte(String sms) {
        int len = sms.length();
        int cnt = 0;
        for (int i = 0; i < len; i++) {
            if (sms.charAt(i) < 256)
                cnt++;
            else
                cnt +=2;
        }
        return cnt;
    }
    
    /** ANT 매칭파일을 리소스로 돌려준다 */
    public static Resource[] resourceByClasspath(String packageAntMatchs) throws IOException{
    	PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    	Iterable<String> matches = Splitter.on(',').trimResults().omitEmptyStrings().split(packageAntMatchs);
    	
    	List<Resource> allResources = Lists.newArrayList();
    	
    	for(String match : matches){
    		Resource[] res = resolver.getResources("classpath:"+match);
    		for(Resource each : res) allResources.add(each);
    	}
    	
    	return allResources.toArray(new Resource[allResources.size()]);
    }
    
}
