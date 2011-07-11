package erwins.webapp.myApp.common.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/** 스프링 내부오류 처리. */
@Component
public class ErrorSpringMVCController extends DefaultHandlerExceptionResolver{

	/*
	@Override
	protected ModelAndView handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		return new ModelAndView("exception/exceptionFormat").addObject("message", ex.getMessage());
	}*/

	@Override
	protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		System.out.println("=========================1");
		return super.handleMissingServletRequestParameter(ex, request, response, handler);
	}

	@Override
	protected ModelAndView handleNoSuchRequestHandlingMethod(NoSuchRequestHandlingMethodException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		System.out.println("=========================2");
		return super.handleNoSuchRequestHandlingMethod(ex, request, response, handler);
	}
	
	

}
