package erwins.jsample.validation;

import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;



public class TestNumber extends TestRoot{
	
	@Test
	public void test() throws InterruptedException{
		
		TestNumberBean vo = new TestNumberBean();
		DataBinder binder = new DataBinder(vo);
		binder.setValidator(validator);
		binder.setConversionService(conversionService);
		
		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("n01", "0121");
		pvs.add("n02", "20");
		pvs.add("n03", "30.22");
		pvs.add("n04", "9.999");
		pvs.add("n05", "50.2");
		binder.bind(pvs);
		binder.validate();
		
		print(binder);
		
		
	}
	
	

}
