package erwins.webapp.myApp.user;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erwins.util.webapp.GenericAppEngineCacheDao;
import erwins.util.webapp.GenericAppEngineCacheService;
import erwins.util.webapp.JExcell;
import erwins.webapp.myApp.Current;

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
	
	@Transactional
	public void saveOrMerge(GoogleUser client){
		Date date = new Date();
		SessionInfo info = Current.getInfo().constraintLogin();
		if(client.getId()==null) {
			client.setCreateDate(date);
			client.setUpdateDate(date);
			saveOrUpdate(client);
		}else{
			GoogleUser server =  dao.getById(client.getId());
			info.constraintAdminOrUser(server);
			server.setNickname(client.getNickname());
			server.setUpdateDate(date);
		}
	}
	
	@Override
	@Transactional
	public void delete(String id){
		GoogleUser server =  dao.getById(id);
		Current.getInfo().constraintLogin().constraintAdminOrUser(server);
		dao.delete(server);
	}

	@Override
	protected GenericAppEngineCacheDao<GoogleUser> getDao() {
		return dao;
	}
	
	
}
