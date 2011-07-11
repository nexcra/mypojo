package erwins.webapp.myApp.user;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import erwins.util.exception.BusinessException;
import erwins.util.morph.MapToBean;
import erwins.util.webapp.AppUtil;
import erwins.util.webapp.JExcell;
import erwins.webapp.myApp.AjaxView;
import erwins.webapp.myApp.Current;

@Controller
@RequestMapping("/user/*")
public class GoogleUserController {
	
	@Autowired private GoogleUserService googleUserService ; 
	@Autowired private MapToBean mapToBean; 
	
	@RequestMapping("/page")
	public String page() {
		return "user/page";
	}
	@RequestMapping("/search")
	public View search() {
		Collection<GoogleUser> list =  googleUserService.findAll();
		AppUtil.rownum(list);
		return new AjaxView(list);
	}
	/** 사실 수정만 된다. */
	@RequestMapping("/save")
	public View save(@RequestParam String save) {
		GoogleUser user = mapToBean.build(save, GoogleUser.class);
		SessionInfo info = Current.getInfo();
		info.constraintLogin();
		if(!user.getId().equals(info.getUser().getId())) throw new BusinessException("해당 사용자만 수정 가능합니다.");
		googleUserService.saveOrMerge(user);
		return new AjaxView("[{0}] 의 데이터 수정완료",user.getGoogleEmail());
	}
	@RequestMapping("/remove")
	public View remove(@RequestParam String removeId) {
		Current.getInfo().constraintByAdmin();
		googleUserService.delete(removeId);
		return new AjaxView("사용자가 정상적으로 삭제되었습니다.");
	}
	@RequestMapping("/download")
	public void download(HttpServletResponse resp) {
		JExcell jxls = new JExcell(resp);
		googleUserService.buildExcell(jxls);
		jxls.write();
	}

}
