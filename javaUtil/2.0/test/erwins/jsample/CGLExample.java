package erwins.jsample;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class CGLExample {

    public static class MemberServiceImpl{
        public MemberServiceImpl() {
            System.out.println("create MemberServiceImpl");
        }
        public void regist(String member) {
            System.out.println("MemberServiceImpl.regist");
        }
        public String getMember(String id) {
            System.out.println("MemberServiceImpl.getMember:"+id);
            return "qwe";
        }
    }
	
	@Test
	public void emptyText() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(MemberServiceImpl.class);
		enhancer.setCallback(new MethodInterceptor() {
			/** MethodProxy가 CGL방식. Method는 자바의 리플렉션  */
			@Override
			public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
				System.out.println("인터셉터 시작");
	            Object returnValue = methodProxy.invokeSuper(object, args);
	            System.out.println("인터셉터 종료");
				return returnValue;
			}
			
		});
		Object obj = enhancer.create();
		MemberServiceImpl memberService = (MemberServiceImpl) obj;
		System.out.println(memberService.getMember("asd"));		
	}
}
