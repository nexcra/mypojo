package erwins.util.morph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import erwins.util.root.EntityId;
import erwins.util.valueObject.Day;


/**
 * 도메인 테스트 도우미.
 */
@SuppressWarnings("serial")
public class DomainTestOld implements EntityId<Long>{
	
	private String name;
	private Long id;
	private int number;
	private BigDecimal decimal;
	
	private Day day;
	private Date date;
	
	private AdressSample adress;
	
	private List<DomainTestOld> list = new ArrayList<DomainTestOld>();
	
	private Collection<DomainTestOld> manyToMany;
	
	private Boolean objectFlag;
	private boolean normalFlag;
	
	private EnumSample ee;

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

	@OneToMany
	public List<DomainTestOld> getList() {
		return list;
	}

	public void setList(List<DomainTestOld> list) {
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

	@ManyToMany
	public Collection<DomainTestOld> getManyToMany() {
		return manyToMany;
	}

	public void setManyToMany(Collection<DomainTestOld> manyToMany) {
		this.manyToMany = manyToMany;
	}
	
	
	
	
	
}