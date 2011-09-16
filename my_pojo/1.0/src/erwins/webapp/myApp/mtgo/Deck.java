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

import erwins.webapp.myApp.Current;
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
	private List<String> cardIds;
	@Persistent
	private List<Integer> quantitys;
	@Persistent
	private String type;
	@Persistent
	private String name;
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
	public List<String> getCards() {
		return cardIds;
	}
	public void setCards(List<String> cards) {
		this.cardIds = cards;
	}
	public List<Integer> getQuantitys() {
		return quantitys;
	}

	public void setQuantitys(List<Integer> quantitys) {
		this.quantitys = quantitys;
	}
	public List<String> getCardIds() {
		return cardIds;
	}

	public void setCardIds(List<String> cardIds) {
		this.cardIds = cardIds;
	}

	public BigDecimal getSumOfPrice() {
		return sumOfPrice;
	}

	public void setSumOfPrice(BigDecimal sumOfPrice) {
		this.sumOfPrice = sumOfPrice;
	}
	@Override
	public int compareTo(Deck o) {
		return type.compareTo(o.type);
	}

	@Override
	public void initValue() {
		Date date = new Date();
		setUpdateDate(date);
		if(id==null) setCreateDate(date);		
	}

	@Override
	public void mergeByClientValue(Deck client) {
		Current.getInfo().constraintByUser(this);
		setColors(client.colors);
		setName(client.name);
		setType(client.type);
	}
	

}
