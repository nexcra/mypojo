
package erwins.util.morph;

import java.util.Date;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import erwins.util.exception.Val;
import erwins.util.tools.DomainTest;

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
    	Val.isEquals(book.getDay().toString(),"2008년06월24일");
    	Val.isTrue(book.getDate()!=null);
    }
}

