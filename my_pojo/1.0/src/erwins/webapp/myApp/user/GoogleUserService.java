package erwins.webapp.myApp.user;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.webapp.GenericAppEngineCacheDao;
import erwins.util.webapp.GenericAppEngineCacheService;
import erwins.util.webapp.JExcell;

@Service
@Transactional(readOnly = true)
public class GoogleUserService extends GenericAppEngineCacheService<GoogleUser>{
	
	@Autowired private GoogleUserDao dao;
	
	public Collection<GoogleUser> findAll(){
		return  dao.findAll();
	}
	public GoogleUser getByGoogleMail(String mail){
		return dao.getByGoogleMail(mail);
	}
	public void buildExcell(JExcell jxls){
		jxls.addSheet("사용자정보","구글메일","닉네임");
		jxls.addSheetConfig(null,null,null);
		Collection<GoogleUser> allUser = dao.findAll();
		for(GoogleUser each : allUser){
			jxls.addValues(each.getGoogleEmail(),each.getNickname());	
		}
	}

	@Override
	protected GenericAppEngineCacheDao<GoogleUser> getDao() {
		return dao;
	}
	
	
}
