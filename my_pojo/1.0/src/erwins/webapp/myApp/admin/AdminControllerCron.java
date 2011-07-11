package erwins.webapp.myApp.admin;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import erwins.webapp.myApp.AjaxView;
import erwins.webapp.myApp.user.GoogleUserService;

@Controller
@RequestMapping("/admin/*")
public class AdminControllerCron{

	protected Log log = LogFactory.getLog(this.getClass());
	
	@SuppressWarnings("unused")
	@Inject private GoogleUserService googleUserService;
	
	/** 벨리데이션 체크를 수행한다. */
	@RequestMapping("/validateReal")
	public View validateReal(HttpServletResponse resp) {
		validate(true);
		return new AjaxView("");
	}
	
	@RequestMapping("/validateTest")
	public View validateTest(HttpServletResponse resp) {
		validate(false);
		return new AjaxView("");
	}

	private void validate(boolean real) {
		log.debug("start validate");
		/*
		Collection<Trx> list = trxService.search(new SearchMap());
		for(Trx each : list){
			SysUser user = sysUserService.get(each.getSysUserId());
			if(user!=null) continue;
			if(real) trxService.delete(each);
			log.info("TRX has been removed");
		}
		*/
		log.debug("end validate");
	}
	

}
