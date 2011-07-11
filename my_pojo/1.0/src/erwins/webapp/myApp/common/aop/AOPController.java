
package erwins.webapp.myApp.common.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import erwins.util.exception.BusinessException;
import erwins.util.exception.LoginRequiredException;
import erwins.util.vender.spring.AbstractAjaxView;
import erwins.webapp.myApp.AjaxView;
import erwins.webapp.myApp.Current;
import erwins.webapp.myApp.Menu;
import erwins.webapp.myApp.user.GoogleUser;
import erwins.webapp.myApp.user.SessionInfo;

/**
 * 인증 / 예외 처리.
 * RoleNotFoundException  ...
 */
@Aspect
@Component
public class AOPController{

    private Log log = LogFactory.getLog(getClass());
    
    @Around("execution(public String erwins.webapp.myApp..*Controller*.*(..))")
    public String jspView(ProceedingJoinPoint joinPoint){
    	log.debug("jsp AOP start");
    	
    	//구글계정으로 로그인 했으나 DB에 없어 null인경우.
    	SessionInfo info = Current.getInfo();
		GoogleUser user = info.getUser();
		if(info.isLogin() && user==null) return "user/newUser";
    	
    	String view;
		try {
			validate(info);
			view = (String) joinPoint.proceed();
		} catch (LoginRequiredException e) {
			view = "redirect:/rest/loginPage"; //~~
		} catch (BusinessException e) {
			throw new RuntimeException(e);
		} catch (Throwable e) {
			log.error(e,e);
			e.printStackTrace();
			return "exception/exceptionUnknown";
		}
		log.debug("jsp AOP end");
        return view;
    }

	private void validate(SessionInfo info) {
		Menu menu = info.getMenu();
		if(menu==null) throw new RuntimeException("메뉴를 찾을 수 없음 {0}. 디버깅 필요. : "+info.getUrl());
		menu.validate(info.getUser());
	}
    
	/** 일반적으로 AjaxView이지만, 예외의 경우 ModelAndView나 void가 올 수 있다. */
    @Around("execution(public org.springframework.web.servlet.View || void erwins.webapp.myApp..*Controller*.*(..))")    
    public AbstractAjaxView ajaxCall(ProceedingJoinPoint joinPoint){
    	log.debug("ajax AOP start");
    	AbstractAjaxView view;
    	SessionInfo info = Current.getInfo();
		try {
			validate(info);
			view = (AjaxView)joinPoint.proceed();
		} catch (LoginRequiredException e) {
			view = new AjaxView("이 기능은 로그인 하셔야 합니다.").isFail();
		} catch (BusinessException e) {
			view = new AjaxView(e.getMessage()).isFail();
		} catch (Throwable e) {
			log.error(e,e);
			e.printStackTrace();
			view = new AjaxView(e.getMessage()).isFail();
		}
		if(view!=null) view.addObject(GoogleUser.class.getSimpleName(), info.getUser());
		log.debug("ajax AOP end");
        return view;
    }
    

}