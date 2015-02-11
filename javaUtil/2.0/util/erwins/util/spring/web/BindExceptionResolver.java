package erwins.util.spring.web;

import javax.validation.Valid;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import erwins.util.lib.ReflectionUtil;
import erwins.util.validation.WebDataValidationException;

/**
 * 스프링 MVC에 파라메터로 매핑해준다.
 * @Valid를 붙였을때  바인딩 예외가 발생하는 경우 예외를 던져준다.
 * 단순히 컨트롤러의 파라메터로 BindingResult를 받기 싫어서 만들어진 리졸버이다.
 * binder.closeNoCatch()를 호출하면 자동으로 ServletRequestBindingException를 던져주지만, 내가만든 형식의 예외로 던지고 싶어서 이렇게 변경했다.
	<mvc:annotation-driven   >
		<mvc:argument-resolvers >
            <bean class="erwins.util.spring.web.BindExceptionResolver"></bean>   
            <bean class="org.springframework.data.web.PageableHandlerMethodArgumentResolver" />  
        </mvc:argument-resolvers>
	</mvc:annotation-driven>
 */
public class BindExceptionResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		Valid validAnnotation = methodParameter.getParameterAnnotation( Valid.class);
		return validAnnotation != null;
	}

	/** 일반적인 변환과 동일하지만 컨트롤러에서 BindingResult를 받지 않고 예외를 던진다.  */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Object vo = ReflectionUtil.newInstance(parameter.getParameterType());
		ExtendedServletRequestDataBinder binder = (ExtendedServletRequestDataBinder) binderFactory.createBinder(webRequest, vo, parameter.getParameterType().getSimpleName());
		ServletWebRequest req = (ServletWebRequest) webRequest;
		binder.bind(req.getRequest());
		binder.validate();
		BindingResult bindingResult = binder.getBindingResult();
		WebDataValidationException.throwExceptionIfHasErrors(bindingResult);
		return vo;
	}
	
}
