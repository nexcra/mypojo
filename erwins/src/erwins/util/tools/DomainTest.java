package erwins.util.tools;

import java.util.Date;

import javax.persistence.Id;

import erwins.util.root.EntityId;
import erwins.util.valueObject.Day;


/**
 * 도메인 테스트 도우미.
 */
public class DomainTest implements EntityId<Long>{
	private Day day;
	private Date date;
	private Long id;
	private String name;

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}