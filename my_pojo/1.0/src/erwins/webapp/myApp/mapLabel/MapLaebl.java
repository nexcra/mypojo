package erwins.webapp.myApp.mapLabel;


import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

import erwins.webapp.myApp.Current;
import erwins.webapp.myApp.GoogleUserEntity;
import erwins.webapp.myApp.RootEntity;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class MapLaebl implements RootEntity<MapLaebl>,Serializable,GoogleUserEntity{

	private static final long serialVersionUID = 1L;
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String id;
    /** 위도(Latitude) */
    @Persistent
    private String lat;
    /** 경도(Longitude) */
    @Persistent
    private String lng;
    @Persistent
    private String label;
    @Persistent
    private Text description;
    @Persistent
    private String displayType;
    @Persistent
    private String googleUserId;
    @NotPersistent
    private String googleUserName;
    @Persistent
    private Date createDate;
    @Persistent
    private Date updateDate;
    @NotPersistent
    private int rownum;
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Text getDescription() {
        return description;
    }
    public void setDescription(Text description) {
        this.description = description;
    }
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDisplayType() {
		return displayType;
	}
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	public String getGoogleUserId() {
		return googleUserId;
	}
	public void setGoogleUserId(String googleUserId) {
		this.googleUserId = googleUserId;
	}
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
	public int getRownum() {
		return rownum;
	}
	public String getGoogleUserName() {
		return googleUserName;
	}
	public void setGoogleUserName(String googleUserName) {
		this.googleUserName = googleUserName;
	}
	@Override
	public int compareTo(MapLaebl o) {
		if(this.getCreateDate()==null || o.getCreateDate()==null) return 0;
		return getCreateDate().compareTo(o.getCreateDate());
	}
	@Override
	public void setRownum(int rownum) {
		this.rownum = rownum;
	}
	@Override
	public void initValue() {
		Date date = new Date();
		setUpdateDate(date);
		if(id==null) setCreateDate(date);
	}
	@Override
	public void mergeByClientValue(MapLaebl client) {
		Current.getInfo().constraintByUser(this);
		setDescription(client.getDescription());
		setDisplayType(client.getDisplayType());
		setLabel(client.getLabel());
		setLat(client.getLat());
		setLng(client.getLng());
	}

}
