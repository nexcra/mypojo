package erwins.webapp.myApp.user;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import erwins.util.lib.CollectionUtil;
import erwins.util.webapp.GenericAppEngineCacheDao;

@Repository
public class GoogleUserDao  extends GenericAppEngineCacheDao<GoogleUser>{
	
	private static final String ORDER = "nickname";
	
	@Override
	public Collection<GoogleUser> findAll(){
		return  findAll(ORDER);
	}
	
	public GoogleUser getByGoogleMail(String googleMail){
		Collection<GoogleUser> result = getJdoTemplate().find(getPersistentClass(),"googleEmail == gmail","String gmail",googleMail);
		return CollectionUtil.getUniqNullable(result);
	}
	
}
