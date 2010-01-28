
package erwins.util.morph;

import java.lang.annotation.Annotation;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import erwins.util.exception.Val;
import erwins.util.morph.Duplicator.DuplicatorConfig;
import erwins.util.tools.DomainTest;
import erwins.util.valueObject.Day;
import erwins.util.valueObject.ValueObject;

public class DuplicatorTest{

    protected static MockHttpServletRequest req = new  MockHttpServletRequest();
    
    @Test
    public void test() throws Exception {
    	Duplicator duplicator = Duplicator.instance();
    	
    	DomainTest server = new DomainTest();
		server.setId(0L);
		server.setName("변경전");
		server.setDay(new Day("20081212"));
		
		DomainTest parameter = new DomainTest();
		parameter.setId(123L);
		parameter.setName("변경후");
		parameter.setDay(new Day("20081111"));
		
		duplicator.shallowCopy(server, parameter);
		Val.isEquals(server.getId(),0L); //ID는 변경되지 않는다.
		Val.isEquals(server.getName(),"변경후");
		Val.isEquals(server.getDay(),new Day("20081111"));
		
		duplicator.add(new DuplicatorConfig() {
			@Override
			public boolean run(String fieldName, Class<?> setterType, Annotation[] annos) {
				return !ValueObject.class.isAssignableFrom(setterType); //Value이면 바뀌지 않게 설정.
			}
		});
		
		DomainTest parameter2 = new DomainTest();
		parameter2.setName("두번째변경");
		parameter2.setDay(new Day("20080101"));
		duplicator.shallowCopy(server, parameter2);
		Val.isEquals(server.getName(),"두번째변경");
		Val.isEquals(server.getDay(),new Day("20081111")); //복사해도 바뀌지 않는다.
    }
    
    
}

