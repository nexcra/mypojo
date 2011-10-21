package erwins.webapp.myApp.mtgo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import erwins.util.temp.StringTemp;
import erwins.webapp.myApp.GoogleUserEntity;
import erwins.webapp.myApp.RootEntity;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Deck implements RootEntity<Deck>, Serializable, GoogleUserEntity {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String id;

	@Persistent
	private List<Card> cards;
	@Persistent
	private String type;
	@Persistent
	private String name;
	@Persistent
	private String description;
	@Persistent
	private List<String> colors;
	@Persistent
	private int win;
	@Persistent
	private int lose;
	@Persistent
	private BigDecimal sumOfPrice;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getColors() {
		return colors;
	}

	public void setColors(List<String> colors) {
		this.colors = colors;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLose() {
		return lose;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGoogleUserId() {
		return googleUserId;
	}

	public void setGoogleUserId(String googleUserId) {
		this.googleUserId = googleUserId;
	}

	public String getGoogleUserName() {
		return googleUserName;
	}

	public void setGoogleUserName(String googleUserName) {
		this.googleUserName = googleUserName;
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

	public void setRownum(int rownum) {
		this.rownum = rownum;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public BigDecimal getSumOfPrice() {
		return sumOfPrice;
	}

	public void setSumOfPrice(BigDecimal sumOfPrice) {
		this.sumOfPrice = sumOfPrice;
	}
	
	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(Deck o) {
		int order = 0;
		order = StringTemp.compareWith(order, type, o.type, true);
		order = StringTemp.compareWith(order, name, o.name,true);
		return order;
	}
	

}
