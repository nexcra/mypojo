package erwins.util.lib;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** LazyLoader할때 역시 사용된다. */ 
public abstract class CglUtil {
    
    private static MethodInterceptor EMPTY = new MethodInterceptor () {
        @Override
        public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return getEmptyObject(method.getReturnType());
        }
    };
    
    /** 해당 타입의 빈 객체를 리턴한다. 단순 null방지용
     * 나중에 리턴타입들 추가 */
    private static Object getEmptyObject(Class<?> clazz) {
        if(Map.class.isAssignableFrom(clazz)) return Collections.EMPTY_MAP;
        if(List.class.isAssignableFrom(clazz)) return Collections.EMPTY_LIST;
        if(Set.class.isAssignableFrom(clazz)) return Collections.EMPTY_SET;
        return null;
    }
    
    /** Enhancer로 해당 인터페이스의 빈 객체를 생성한다.
     * 예외 발생 등으로 해당 객체를 생성하지 못했을때 무시하기 위해 사용된다.  */
    public static <T>  T createEmptyInstance(Class<T> interfaceOfTarget) {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{interfaceOfTarget});
        enhancer.setCallback(EMPTY);
        Object obj = enhancer.create();
        @SuppressWarnings("unchecked")
        T memberService = (T)obj;
        return memberService;
    }
    
    /** 예외가 나지 않는 프록시를 리턴한다
     * 이미 CGL가 적용된(스프링이 생성한) 애들은 proxyMethod를 사용하면 안된다. 주의할것 */
    public static <T>  T createNoExceptionProxy(final T target,Class<T> clazz) {
        final String className = clazz.getName();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor (){
            private Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public Object intercept(Object arg0, Method method, Object[] arg2, MethodProxy proxyMethod) throws Throwable {
                try {
                    return method.invoke(target, arg2);
                } catch (Exception e) {
                    log.warn("error on Proxy {}. but ignored : {}",className,e.getMessage());
                    return getEmptyObject(method.getReturnType());
                }
            }
        });
        Object obj = enhancer.create();
        @SuppressWarnings("unchecked")
        T memberService = (T)obj;
        return memberService;
    }
    
    
    


}
