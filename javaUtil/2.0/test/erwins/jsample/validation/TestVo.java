package erwins.jsample.validation;

import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;



public class TestVo extends TestRoot{
	
	
	@Test
	public void test() throws InterruptedException{
		
		TestVoBean vo = new TestVoBean();
		DataBinder binder = new DataBinder(vo);
		binder.setValidator(validator);
		binder.setConversionService(conversionService);
		
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("date01.start",  "2015-03-27 12:18");
		pvs.add("date01.end",    "2015-03-27 12:17");
		
		//pvs.add("date02.start",    "");
		pvs.add("date02.end",    "2015-03-27 12:17");
		
		
		binder.bind(pvs);
		binder.validate();
		//binder.validate(TestString01Group.class);
		
		
		print(binder);
		
		System.out.println(vo.getDate02().getStart());
		System.out.println(vo.getDate02().getEnd());
		
	}
	
	

}
