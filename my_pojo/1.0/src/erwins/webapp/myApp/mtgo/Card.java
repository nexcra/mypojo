package erwins.webapp.myApp.mtgo;
import java.math.BigDecimal;


public class Card {
	private String cardName;
	private String type;
	private String rarity;
	private String cost;
	private String price;
	private Integer quantity;
	private String edition;
	private Integer matchSize; //이름으로 검색된 카드 판본수
	private String url;
	
	private BigDecimal money; //단순히 계산하기위한 용도임
	
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
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
	public Integer getMatchSize() {
		return matchSize;
	}
	public void setMatchSize(Integer matchSize) {
		this.matchSize = matchSize;
	}
	
	
	
	
}
