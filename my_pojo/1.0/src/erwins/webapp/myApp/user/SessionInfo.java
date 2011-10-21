package erwins.webapp.myApp.user;

import erwins.util.exception.BusinessException;
import erwins.util.exception.LoginRequiredException;
import erwins.util.lib.CollectionUtil;
import erwins.webapp.myApp.GoogleUserEntity;
import erwins.webapp.myApp.Menu;


public class SessionInfo{
	
	private String url;
	private GoogleUser user;
	private boolean login;
	private Menu menu;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public GoogleUser getUser() {
		return user;
	}
	public void setUser(GoogleUser user) {
		this.user = user;
	}
    public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	/** Google계정 로그인을 말한다. */
	public boolean isLogin() {
		return login;
	}
	public boolean isRoleAble(String role) {
		if(user==null) return false;
		return CollectionUtil.isEqualsAny(user.getRoles(),role);
	}
	public boolean isAdmin() {
		return isRoleAble(GoogleUser.ROLE_ADMIN);
	}
	public void initGoogleId(GoogleUserEntity entity) {
		entity.setGoogleUserId(getUser().getId());
	}
	public void setLogin(boolean login) {
		this.login = login;
	}
	
	
	public SessionInfo constraintLogin() {
		if(!isLogin()) throw new LoginRequiredException();
		return this;
	}
	
	public SessionInfo constraintByAdmin() {
		if(!isRoleAble(GoogleUser.ROLE_ADMIN)) throw new BusinessException("관리자 권한만 가능합니다.");
		return this;
	}
	
	/** 해당 사용자의 권한을 확인하거나 , 사용자를 입력해준다. */
	public SessionInfo constraintByUser(GoogleUserEntity doc) {
		String id = doc.getGoogleUserId();
		if(!user.getId().equals(id)) throw new BusinessException("해당 문서의 작성자만 처리 가능합니다."); 
		return this;
	}
	
	public SessionInfo constraintAdminOrUser(GoogleUserEntity doc) {
		if(!user.getId().equals(doc.getGoogleUserId())) constraintByAdmin();
		return this;
	}

}
