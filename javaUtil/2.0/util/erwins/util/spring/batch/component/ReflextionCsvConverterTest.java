package erwins.util.spring.batch.component;

import org.junit.Test;

import erwins.util.root.PairObject;
import erwins.util.text.StringUtil;

public class ReflextionCsvConverterTest{
	
	@Test
	public void main() throws Exception {
		
		ReflextionCsvConverter<PairObject> asd = ReflextionCsvConverter.create(PairObject.class);
		PairObject vo = new PairObject();
		vo.setName("멍멍");
		vo.setValue("34");
		System.out.println(vo);
		String[] line = asd.aggregate(vo);
		System.out.println(StringUtil.join(line, ","));
		PairObject re = asd.mapLine(line, 1);
		
		System.out.println(re);
		
	}
	

	

}
