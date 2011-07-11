package erwins.webapp.myApp.admin;

import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import erwins.util.webapp.AppJExcell;
import erwins.webapp.myApp.AjaxView;
import erwins.webapp.myApp.Current;
import erwins.webapp.myApp.SystemInfo;
import erwins.webapp.myApp.user.GoogleUser;
import erwins.webapp.myApp.user.GoogleUserDao;
import erwins.webapp.myApp.user.GoogleUserService;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

	private static final String UPLOAD_URL_TRX = "/rest/admin/trx/upload";
	private static final String UPLOAD_URL_USER = "/rest/admin/user/upload";
	
	/** ㅅㅂ 돈안내면 업로드 불가. */
	@RequestMapping("/page")
	public String page(HttpServletRequest req) {
		if(!SystemInfo.isServer()){
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			String  trxUpload = blobstoreService.createUploadUrl(UPLOAD_URL_TRX);
			req.setAttribute("trxUpload", trxUpload);	
			String  userUpload = blobstoreService.createUploadUrl(UPLOAD_URL_USER);
			req.setAttribute("userUpload", userUpload);	
		}
		return "admin";
	}
	
	@Inject private GoogleUserDao googleUserDao;
	@Inject private GoogleUserService googleUserService;
	
	/** 서버 재기동후 시리얼라이징이 틀린경우가 있다. 싹 초기화하자~ */
	@RequestMapping("/refresh")
	public View refresh(HttpServletResponse resp) {
		googleUserDao.clear();
		return new AjaxView("");
	}
	
	@RequestMapping("/user/download")
	public void userDownload(HttpServletResponse resp) {
		Collection<GoogleUser> sysUsers = googleUserService.findAll();
		AppJExcell.donwloadToJSON(resp, sysUsers);
	}
	@RequestMapping("/user/upload")
	public String userUpload(HttpServletRequest req) {
		Collection<JSONObject> result = AppJExcell.uploadFromJSON(req,"sysUser");
		for(JSONObject each : result){
			each.put("roles", null);
			GoogleUser entity = Current.to.build(each, GoogleUser.class);
			googleUserService.saveOrUpdate(entity);
		}
		return "redirect:/rest/admin/page";
	}

}
