package erwins.webapp.myApp.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import erwins.webapp.myApp.RootEntity;

@SuppressWarnings("serial")
@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class GoogleUser implements RootEntity<GoogleUser>,Serializable{
	
	//private static final long serialVersionUID = -1860646969953562965L;
	public static final String ROLE_USER = "user";
	public static final String ROLE_ADMIN = "admin";
	
	/** 로그인 사용자는 기본 Role을 가진다. */
	public GoogleUser(){
		this.addRoles(ROLE_USER);
	}
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String id;
	@Persistent
	private String nickname;
	@Persistent
	private String googleEmail;
	@Persistent
	private Date createDate;
	@Persistent
	private Date updateDate;
	@NotPersistent
	private Set<String> roles = new HashSet<String>();
	@NotPersistent
	private int rownum;
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}



	public Date getUpdateDate() {
		return updateDate;
	}



	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getNickname() {
		return nickname;
	}



	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getGoogleEmail() {
		return googleEmail;
	}



	public void setGoogleEmail(String googleEmail) {
		this.googleEmail = googleEmail;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	public void addRoles(String role) {
		this.roles.add(role);
	}
	public int getRownum() {
		return rownum;
	}



	public void setRownum(int rownum) {
		this.rownum = rownum;
	}



	@Override
	public int compareTo(GoogleUser o) {
		int order = nickname.compareTo(o.getNickname());
		return order;
	}
	@Override
	public void initValue() {
		Date date = new Date();
		setUpdateDate(date);
		if(id==null) setCreateDate(date);
	}
	@Override
	public void mergeByClientValue(GoogleUser client) {
		setNickname(client.getNickname());
	}
}
