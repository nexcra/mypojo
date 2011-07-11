package erwins.webapp.myApp;

import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.PostConstruct;

import net.sf.json.util.JSONUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.UserService;

import erwins.util.exception.BusinessException;
import erwins.util.exception.LoginRequiredException;
import erwins.util.lib.StringUtil;
import erwins.util.morph.MapToBean;
import erwins.util.morph.MapToBeanRoot.MapToBeanBaseConfig;
import erwins.util.morph.MapToBeanRoot.MapToBeanConfigFetcher;
import erwins.util.webapp.GoogleXMPP;
import erwins.webapp.myApp.user.GoogleUser;
import erwins.webapp.myApp.user.GoogleUserService;
import erwins.webapp.myApp.user.SessionInfo;

@Controller
public class DefaultController {
	/** 이하 2개 귀찮아서 static으로 정의 */
	public static String LOGIN_URL; 
	public static String LOGOUT_URL;
	
	@Autowired private UserService userService;
	@Autowired private ChannelComponent channelHelper;
	@Autowired private GoogleUserService googleUserService;
	@Autowired private MapToBean mapToBean;
	
	@PostConstruct
	public void init(){
		LOGIN_URL  = userService.createLoginURL("/rest/index");
		LOGOUT_URL = userService.createLogoutURL("/rest/index");
		mapToBean.addConfig(new MapToBeanBaseConfig(new Class[] {Text.class},
				new MapToBeanConfigFetcher() {
			@Override
			public Object fetch(Field field, @SuppressWarnings("rawtypes") Map map) {
				Object value = map.get(field.getName()); 
				if(JSONUtils.isNull(value)) return null;
				return new Text(value.toString());
			}
		}));
	}
	
	@RequestMapping("index")
	public String index() {
		return "index";
	}
	/** 테스트용 */
	@RequestMapping("none/test")
	public String test() {
		return "test";
	}
	
	@RequestMapping("none/ping")
	public View ping() {
		return new AjaxView("ping");
	}
	
	/** 이놈은 비로그인 시에도 사용되어야 한다. 구글인증 때문에 조금 특이하게 변경된 경우  */
	@RequestMapping("none/editNickname")
	public View editNickname(@RequestParam(defaultValue="") String nickName) {
		if(!userService.isUserLoggedIn()) throw new LoginRequiredException();
		if(StringUtil.isEmpty(nickName)) throw new BusinessException("닉네임을 입력하셔야 합니다");
		
		String mailAddress = userService.getCurrentUser().getEmail();
		GoogleUser user = googleUserService.getByGoogleMail(mailAddress);
		if(user == null){
			user = new GoogleUser();
			user.setGoogleEmail(mailAddress);
		}
		user.setNickname(nickName);
		googleUserService.saveOrUpdate(user);
		return new AjaxView("OK");
	}
	
	@RequestMapping("none/channel/chat")
	public View channelChat(@RequestParam(defaultValue="...") String message) {
		SessionInfo info = Current.getInfo(); 
		info.constraintLogin();
		GoogleUser user = info.getUser();
		String token = channelHelper.getOrCreateToken(user.getId());
		channelHelper.broadcastSimpleMessage(token,"["+user.getNickname()+"] : "+message);
		GoogleXMPP.send(Config.ADMIN_ID[0], message);
		return new AjaxView(String.valueOf(channelHelper.size()));
	}
	
	@RequestMapping("none/channel/create")
	public View channelCreate() {
		SessionInfo info = Current.getInfo(); 
		info.constraintLogin();
		GoogleUser user = info.getUser();
		String token = channelHelper.getOrCreateToken(user.getId());
		channelHelper.broadcastSimpleMessage(token,"[{0}]님께서 접속하셨습니다", info.getUser().getNickname());
		return new AjaxView(token);
	}
	
	@RequestMapping("none/channel/remove")
	public View channelRemove(@RequestParam String key) {
		channelHelper.remove(key);
		return new AjaxView("channelRemove");
	}

}
