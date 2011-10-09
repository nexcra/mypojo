package erwins.webapp.myApp.mtgo;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import erwins.webapp.myApp.RootEntity;

@PersistenceCapable
public class Card implements RootEntity<Card>, Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String id;
	
	@Persistent
	private String cardName;
	@Persistent
	private String type;
	@Persistent
	private String rarity;
	@Persistent
	private String cost;
	@Persistent
	private String price;
	@Persistent
	private String edition;
	@Persistent
	private String url;
	@Persistent
	private Integer quantity;
	@NotPersistent
	private BigDecimal money; //단순히 계산하기위한 용도임
	@NotPersistent
	int rownum;
	
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public int compareTo(Card o) {
		if(price==null || o.price==null) return 0;
		int order = new BigDecimal(price).compareTo(new BigDecimal(o.price)) * -1;
		if(order==0) order = cardName.compareTo(o.cardName); 
		return order;
	}
	public int getRownum() {
		return rownum;
	}
	public void setRownum(int rownum) {
		this.rownum = rownum;
	}
	@Override
	public void initValue() {
	}
	@Override
	public void mergeByClientValue(Card client) {
		
	}
	
}
