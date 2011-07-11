package erwins.webapp.myApp;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import erwins.webapp.myApp.user.GoogleUser;
import erwins.webapp.myApp.user.GoogleUserService;

public abstract class RootController {
	
	@Autowired protected GoogleUserService googleUserService ;
	
	protected <T extends GoogleUserEntity> void  initGoogleUser(Collection<T> list) {
		for(T each : list){
    		if(each.getGoogleUserId()==null) continue;
    		GoogleUser user = googleUserService.get(each.getGoogleUserId());
    		if(user==null) each.setGoogleUserName("FK오류");
    		else each.setGoogleUserName(user.getNickname());
    	}
	}

}
