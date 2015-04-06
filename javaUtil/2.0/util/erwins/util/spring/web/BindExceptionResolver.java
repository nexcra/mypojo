package erwins.util.spring.web;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import erwins.util.spring.SpringUtil;
import erwins.util.validation.ValidationUtil;
import erwins.util.validation.WebDataValidationException;

/**
 * 스프링 MVC에 파라메터로 매핑해준다.
 * @Valid를 붙였을때  바인딩 예외가 발생하는 경우 예외를 던져준다.
 * 단순히 컨트롤러의 파라메터로 BindingResult를 받기 싫어서 만들어진 리졸버이다.
 * binder.closeNoCatch()를 호출하면 자동으로 ServletRequestBindingException를 던져주지만, 내가만든 형식의 예외로 던지고 싶어서 이렇게 변경했다.
 * 
 * SpringUtil.resolveArgument <-- 이거 병신같다. 나중에 수정하자. 스프링에서 만든게 다들 제각각이라..
 * XML에서 순서 주의할것!! 특이 사항은 먼저 적용해야 한다.
	<mvc:annotation-driven   >
		<mvc:argument-resolvers >
            <bean class="erwins.util.spring.web.BindExceptionResolver"></bean>   
        </mvc:argument-resolvers>
	</mvc:annotation-driven>
 */
public class BindExceptionResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return ValidationUtil.isValidationMethod(methodParameter);
	}

	/** 일반적인 변환과 동일하지만 컨트롤러에서 BindingResult를 받지 않고 예외를 던진다.  */
	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		BindingResult bindingResult = SpringUtil.resolveArgument(methodParameter, webRequest, binderFactory);
		WebDataValidationException.throwExceptionIfHasErrors(bindingResult);
		return bindingResult.getTarget();
	}
	
}
