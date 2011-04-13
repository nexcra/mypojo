
package erwins.util.morph;

import java.lang.annotation.Annotation;

import org.junit.Test;

import erwins.util.exception.Check;
import erwins.util.morph.Duplicator.DuplicatorConfig;
import erwins.util.valueObject.Day;
import erwins.util.valueObject.ValueObject;

public class DuplicatorTest{

    @Test
    public void test() throws Exception {
    	Duplicator duplicator = Duplicator.instance();
    	
    	DomainTestOld server = new DomainTestOld();
		server.setId(0L);
		server.setName("변경전");
		server.setDay(new Day("20081212"));
		
		DomainTestOld parameter = new DomainTestOld();
		parameter.setId(123L);
		parameter.setName("변경후");
		parameter.setDay(new Day("20081111"));
		
		duplicator.shallowCopy(server, parameter);
		Check.isEquals(server.getId(),0L); //ID는 변경되지 않는다.
		Check.isEquals(server.getName(),"변경후");
		Check.isEquals(server.getDay(),new Day("20081111"));
		
		duplicator.add(new DuplicatorConfig() {
			@Override
			public boolean run(String fieldName, Class<?> setterType, Annotation[] annos) {
				return !ValueObject.class.isAssignableFrom(setterType); //Value이면 바뀌지 않게 설정.
			}
		});
		
		DomainTestOld parameter2 = new DomainTestOld();
		parameter2.setName("두번째변경");
		parameter2.setDay(new Day("20080101"));
		duplicator.shallowCopy(server, parameter2);
		Check.isEquals(server.getName(),"두번째변경");
		Check.isEquals(server.getDay(),new Day("20081111")); //복사해도 바뀌지 않는다.
    }
    
    
}

