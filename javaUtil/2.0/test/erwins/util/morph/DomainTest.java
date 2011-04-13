package erwins.util.morph;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import erwins.util.root.DomainObject;
import erwins.util.root.EntityId;
import erwins.util.valueObject.Day;


/**
 * 도메인 테스트 도우미.
 */
@SuppressWarnings("serial")
public class DomainTest implements EntityId<Long> ,DomainObject{
	
	private String name;
	private Long id;
	private Long bigNumber;
	private int number;
	private BigDecimal decimal;
	private Boolean objectFlag;
	private boolean normalFlag;
	
	private EnumSample ee;
	private Day day;
	private Date date;
	private AdressSample adress;
	
	private List<String> simpleList;
	private List<DomainTest> list;
	private Collection<DomainTest> manyToMany;
	private DomainTest parent;
	

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Long getBigNumber() {
		return bigNumber;
	}

	public void setBigNumber(Long bingNumber) {
		this.bigNumber = bingNumber;
	}

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AdressSample getAdress() {
		return adress;
	}

	public void setAdress(AdressSample adress) {
		this.adress = adress;
	}

	public BigDecimal getDecimal() {
		return decimal;
	}

	public void setDecimal(BigDecimal decimal) {
		this.decimal = decimal;
	}

	public List<DomainTest> getList() {
		return list;
	}

	public void setList(List<DomainTest> list) {
		this.list = list;
	}

	public Boolean getObjectFlag() {
		return objectFlag;
	}

	public void setObjectFlag(Boolean objectFlag) {
		this.objectFlag = objectFlag;
	}

	public boolean isNormalFlag() {
		return normalFlag;
	}

	public void setNormalFlag(boolean normalFlag) {
		this.normalFlag = normalFlag;
	}

	public EnumSample getEe() {
		return ee;
	}

	public void setEe(EnumSample ee) {
		this.ee = ee;
	}

	public Collection<DomainTest> getManyToMany() {
		return manyToMany;
	}

	public void setManyToMany(Collection<DomainTest> manyToMany) {
		this.manyToMany = manyToMany;
	}

	public DomainTest getParent() {
		return parent;
	}

	public void setParent(DomainTest parent) {
		this.parent = parent;
	}

	public List<String> getSimpleList() {
		return simpleList;
	}

	public void setSimpleList(List<String> simpleList) {
		this.simpleList = simpleList;
	}
	
	
}