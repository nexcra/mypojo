package erwins.jsample.validation;

import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;



public class TestString01 extends TestRoot{
	
	
	@Test
	public void test() throws InterruptedException{
		TestStringBean01 vo = new TestStringBean01();
		DataBinder binder = new DataBinder(vo);
		binder.setValidator(validator);
		binder.setConversionService(conversionService);
		
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("s01",  "2015-03-27  827364782364287");
		pvs.add("s02",  "2015-03-30");
		pvs.add("s03",  "2015-03-30 15:59");
		pvs.add("s04",  "2016-12-30");
		pvs.add("s05",  "abced1한");
		pvs.add("s06",  "aab");
		pvs.add("s07",  "abcio72349lkj^^");
		pvs.add("s08",  "abcIBF12984G72349lkj^^");
		pvs.add("email","  asd@gmail.com ");
		pvs.add("url", "http://asdsa.com");
		pvs.add("cno", "2534846");
		pvs.add("startDate", "2015");
		pvs.add("endDate", "2016");
		pvs.add("num", "54");
		
		
		binder.bind(pvs);
		binder.validate();
		//binder.validate(TestString01Group.class);
		
		
		print(binder);
		
	}
	
	

}
