
package erwins.util.morph;

import java.util.Date;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import erwins.util.exception.Val;
import erwins.util.root.EntityId;
import erwins.util.valueObject.Day;

public class DissolverTest{

    protected static MockHttpServletRequest req = new  MockHttpServletRequest();
    
    @Test
    public void test() throws Exception {
        req.addParameter("id", "50");
    	req.addParameter("name", "testMe");
    	req.addParameter("day", "20080624");
    	req.addParameter("date", String.valueOf( new Date().getTime()));
    	DomainTest book = Dissolver.instance().getBean(req, DomainTest.class);
    	Val.isEquals(book.getId(),50L);
    	Val.isEquals(book.getName(),"testMe");
    	Val.isEquals(book.getDay().toString(),"2008년6월4일");
    	Val.isTrue(book.getDate()!=null);
    }
    
    public static class DomainTest implements EntityId<Long>{
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

		@Override
		public Long getId() {
			return id;
		}

		@Override
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
}

