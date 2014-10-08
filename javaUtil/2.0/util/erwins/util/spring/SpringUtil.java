package erwins.util.spring;

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
import java.util.concurrent.TimeUnit;

import javax.validation.ValidationException;

import lombok.Data;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.remoting.httpinvoker.CommonsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import erwins.util.lib.ReflectionUtil;
import erwins.util.root.NameValue;
import erwins.util.root.exception.IORuntimeException;
import erwins.util.text.StringUtil;

/** 스프링용 유틸 모음 */
public abstract class SpringUtil {
	
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
		Method m = ReflectionUtil.findField(clazz, joinPoint.getSignature().getName(), joinPoint.getArgs().length);
		return m.getAnnotation(annoClazz);
	}
	
    /** 해당 url의 인터페이스로 RMI객체를 리턴한다.
     * 해당 URL의 WAS에는 스프링 RMI객체를 서비스할 수 있도록 설정이 되어있어야 한다.
     * timeoutSec를 줘서 무한대기를 방지하도록 하자
     * 여기서는 간단한 예제이며, connection pooling, 인증등이 필요하면 새로 만들자.
     * @see http://starplatina.tistory.com/402 */
    @SuppressWarnings("unchecked")
    public static <T> T convertToRmiInstance(String url,Class<T> clazz,Integer timeoutSec){
        HttpInvokerProxyFactoryBean httpProxy = new HttpInvokerProxyFactoryBean();
        httpProxy.setServiceUrl(url);
        httpProxy.setServiceInterface(clazz);
        if(timeoutSec!=null){
        	CommonsHttpInvokerRequestExecutor httpInvokerRequestExecutor = new CommonsHttpInvokerRequestExecutor();
            httpInvokerRequestExecutor.setReadTimeout((int)TimeUnit.SECONDS.toMillis(timeoutSec));
            httpInvokerRequestExecutor.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(timeoutSec));	
            httpProxy.setHttpInvokerRequestExecutor(httpInvokerRequestExecutor);
        }
        
        httpProxy.afterPropertiesSet();
        T rmi = (T) httpProxy.getObject();
        return rmi;
    }
    
    /** xml의 파라메터로 받는 리소스를 File로 변환한다.  IOException 제거 */
    public static List<File> toFiles(Resource[] mappingLocations) {
        List<File> locationSqlFiles = new ArrayList<File>();
        for(Resource each : mappingLocations){
            try {
                locationSqlFiles.add(each.getFile());
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return locationSqlFiles;
    }
    
    public static Resource[] toResources(File[] files) {
    	if(files==null) return new Resource[0];
    	Resource[] resources = new Resource[files.length];
    	for(int i=0;i<files.length;i++){
    		resources[i] = new FileSystemResource(files[i]);
    	}
    	return resources;
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
    public static Resource[] getEmptyResources(){
        return new Resource[]{getEmptyResource()};
    }
    
    /** 어노테이션 / 메소드 매핑으로 등록된 애들도 함께 보여준다 */
    @Deprecated
    public static List<UrlMap> getHandlerMapType(AbstractUrlHandlerMapping handlerMapping){
        Map<String,UrlMap> map =  new TreeMap<String, UrlMap>();
        
        for(Entry<String,Object> e : handlerMapping.getHandlerMap().entrySet()){
            String urlName = e.getKey();
            if(urlName.endsWith(".*") || urlName.endsWith("/")) continue;
            
            Object controller = e.getValue();
            String controllerName = StringUtil.getFirst(controller.toString(), "@");
            Class<?> clazz = ReflectionUtil.forName(controllerName);
            
            if(urlName.endsWith("*")){
                List<Method> methods =  ReflectionUtil.findMethod(clazz, RequestMapping.class);
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
                //Method method = ReflectionUtil.getMethodByName(clazz, methodName);
                Method method = ReflectionUtil.findMethod(clazz, methodName);
                if(method == null) method = findRequest(clazz, methodName);
                
                UrlMap um = new UrlMap();
                um.url = urlName;
                um.method = method;
                um.clazz = clazz;
                map.put(um.url, um);
            }

        }
        return Lists.newArrayList(map.values());
    }
    
    /** rest를 사용하기도 함으로 url을 변형하지 않고 그냥 다 보여준다.
     * 프로젝트마다 변형해서 사용할것  */
    public static List<UrlMap> getHandlerMapType(RequestMappingHandlerMapping handlerMapping){
        Map<String,UrlMap> map =  new TreeMap<String, UrlMap>();
        for(Entry<RequestMappingInfo, HandlerMethod> e : handlerMapping.getHandlerMethods().entrySet()){
        	RequestMappingInfo mappingInfo = e.getKey();
        	HandlerMethod value = e.getValue();
        	for(String pattern : mappingInfo.getPatternsCondition().getPatterns()){
        		UrlMap um = new UrlMap();
                um.url = pattern;
                um.method = value.getMethod();
                um.clazz = value.getBeanType();
                um.url = pattern;
        		map.put(um.url, um);
        	}
    	}
        return Lists.newArrayList(map.values());
    }
    
    /** 해당 methodName으로 매핑된 메소드를 찾아낸다. 
     * rest가 아닌 XXX.do 이런식의 매핑을 찾을때 사용한다. */
    @SuppressWarnings("rawtypes")
	private static Method findRequest(Class clazz, String methodName) {
        List<Method> methods =  ReflectionUtil.findMethod(clazz, RequestMapping.class);
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
    
    /** ANT 매칭파일을 리소스로 돌려준다.
     * XML 파일 등을 가져올때 사용 */
    @Deprecated
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
    
    public static enum AntResourceType{
    	classpath,file;
    }
    
    public static Resource[] antToResources(AntResourceType antResourceType,String antMatchs) throws IOException{
    	PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    	Iterable<String> matches = Splitter.on(',').trimResults().omitEmptyStrings().split(antMatchs);
    	
    	List<Resource> allResources = Lists.newArrayList();
    	
    	for(String match : matches){
    		Resource[] res = resolver.getResources(antResourceType.name()+":"+match);
    		for(Resource each : res) allResources.add(each);
    	}
    	
    	return allResources.toArray(new Resource[allResources.size()]);
    }
    
    
    /**
     * http://docs.spring.io/spring/docs/3.0.x/reference/expressions.html 
     * EL 표현식으로 템플릿 문자열을 생성한다.
     * 다양한 활용이 가능하다. 간단한 구문에 사용하자
     * ex) random number is #{[vo].rowSize} */
    public static String elFormat(String pattern,Object param){
    	ExpressionParser parser = new SpelExpressionParser();
    	return parser.parseExpression(pattern, new TemplateParserContext()).getValue(param,String.class);
    }
    
    /** 
     * MAP을 다이렉트로 지원하지 않는거 같다..
     * ex) SpringUtil.elMapFormat("번호 #{map['accId']}  : #{map['rowNum'] - 3}", m) */
    public static String elMapFormat(String pattern,Map<String,Object> param){
    	ExpressionParser parser = new SpelExpressionParser();
    	ElVoMap map = new ElVoMap();
    	map.setMap(param);
    	return parser.parseExpression(pattern, new TemplateParserContext()).getValue(map,String.class);
    }
    
    @Data
    private static class ElVoMap{
    	private Map<String,Object> map;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T elValue(ExpressionParser parser,String el,Object vo){
    	if(parser==null) parser = new SpelExpressionParser();
    	Expression exp = parser.parseExpression(el);
		return (T) exp.getValue(vo);
    }
    
    /** 필드 예외가 발견되었다면 예외를 던진다. 
     *  예외 포매팅을 정교하게 하려면 별도의 예외 클래스를 작성하자. */
    public static void throwIfFieldError(BindingResult bindingResult){
    	if(bindingResult.hasErrors()){
    		String msg = fieldErrorToString(bindingResult.getAllErrors(),"\n");
    		throw new ValidationException(msg);
    	}
    }
    
    public static String fieldErrorToString(List<ObjectError> errorList,String separator){
    	List<String> msg = Lists.newArrayList();
    	for(ObjectError e :  errorList){
    		if(!(e instanceof FieldError)) throw new IllegalStateException("FieldError가 아닌 예외가 발견되었습니다. "+ e);
			FieldError error = (FieldError) e ;
			msg.add(SpringUtil.elFormat("#{field} --> #{defaultMessage} 입력값 = [#{rejectedValue}],  ", error));
    	}
    	return Joiner.on(separator).join(msg); 
    }
    
    
    /** 스프링 태그에 사용되는 기본 MAP을 리턴한다. */
    @SuppressWarnings("unchecked")
	public static <T extends Enum<?>>  Map<String, String> enumToSpringTagMap(Class<T> clazz) {
		Enum<?>[] ins = clazz.getEnumConstants();
		Map<String,String> tag = new ListOrderedMap();
		for(Enum<?> each : ins){
			if(each instanceof NameValue){
				NameValue nv = (NameValue) each;
				tag.put(nv.getValue(), nv.getName()); // ID / NAME 순이다.
			}else tag.put(each.name(), each.name()); 
			
		}
		return tag;
	}
    
	/** 단어를 잘게 나눈다. 주로 like 검사를 할때 사용된다. 
	 *  %문자% */
	public static List<String> splitWord(String text,int minLength){
		List<String> words = Lists.newArrayList();
		for(int i=0;i<=text.length();i++){ // <= 이다 주의.
			for(int j=0;j<i;j++){
				String subkey = text.substring(j,i);
				if(subkey.length() < minLength) continue;
				words.add(subkey);
			}
		}
		return words;
	}
	
	/** 단어를 잘게 나눈다. 주로 like 검사를 할때 사용된다.
	 *  %문자 =>  */
	public static List<String> splitWordSuffix(String text,int minLength){
		List<String> words = Lists.newArrayList();
		for(int i=0;i<=text.length();i++){ // <= 이다 주의.
			String subkey = text.substring(i,text.length());
			if(subkey.length() < minLength) continue;
			words.add(subkey);
		}
		return words;
	}
	
	/** 단어를 잘게 나눈다. 주로 like 검사를 할때 사용된다.
	 *  %문자 =>  */
	public static List<String> splitWordPrefix(String text,int minLength){
		List<String> words = Lists.newArrayList();
		for(int i=0;i<=text.length();i++){ // <= 이다 주의.
			String subkey = text.substring(0,i);
			if(subkey.length() < minLength) continue;
			words.add(subkey);
		}
		return words;
	}
    
}
