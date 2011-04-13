package erwins.util.morph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.Validate;
import org.junit.Test;

import erwins.util.valueObject.Day;

/**
 * 테스트를 null로  인식하는걸로 또 만들것.
 * BeanToJson  역시 테스트 한다?.
 */
public class MapToBeanTest {
	
	private final MapToBean m = MapToBean.create();
	private final BeanToJson b = BeanToJson.create();

	@Test
	public void test() {
		DomainTest demo = new DomainTest();
		defaultValue(demo);
		domainObject(demo);
		collection(demo);
		
		JSONObject jsonByDemo = (JSONObject) b.build(demo);
		DomainTest domainFromJson = m.build(jsonByDemo, DomainTest.class);
		
		JSONObject jsonByDemo2 = (JSONObject) b.build(domainFromJson);
		Validate.isTrue(jsonByDemo.equals(jsonByDemo2));
	}
	
	@Test
	public void jsonTest() {
		JSONObject json = new JSONObject();
		json.put("simpleList", new String[]{"A","B"});
		DomainTest A = new DomainTest();
		DomainTest B = new DomainTest();
		A.setName("A");
		B.setName("B");
		json.put("list", new DomainTest[]{A,B});
		DomainTest domain = m.build(json, DomainTest.class);
		
		Validate.isTrue(domain.getSimpleList().get(0).equals("A"));
		Validate.isTrue(domain.getList().get(1).getName().equals("B"));
	}
	
	@Test /** request같은거. */
	public void mapTest() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "이름");
		map.put("bigNumber", 55);
		map.put("decimal", "548786.587867");
		map.put("simpleList", new String[]{"a","b"});
		
		DomainTest domain = m.build(map, DomainTest.class);
		Validate.isTrue("이름".equals(domain.getName()));
		Validate.isTrue(domain.getId()==null);
		Validate.isTrue(new Long(55).equals(domain.getBigNumber()));
		Validate.isTrue(new BigDecimal("548786.587867").equals(domain.getDecimal()));
		Validate.isTrue("a".equals(domain.getSimpleList().get(0)));
	}

	private void collection(DomainTest demo) {
		List<String> simpleList = new ArrayList<String>(); 
		simpleList.add("A");
		simpleList.add("B");
		demo.setSimpleList(simpleList);

		DomainTest a1 = new DomainTest();
		a1.setName("a1");
		DomainTest a2 = new DomainTest();
		a2.setName("a2");
		
		List<DomainTest> list = new ArrayList<DomainTest>(); 
		list.add(a1);
		list.add(a2);
		demo.setList(list);
	}

	private void domainObject(DomainTest demo) {
		AdressSample adress = new AdressSample();
		adress.setAdressName("부산시 동래구");
		adress.setPost("666-879");
		demo.setAdress(adress);
		
		DomainTest parent = new DomainTest();
		parent.setName("부모");
		demo.setParent(parent);
	}

	private void defaultValue(DomainTest demo) {
		demo.setName("이름");
		demo.setEe(EnumSample.BBB);
		demo.setNumber(137);
		demo.setBigNumber(84871231231233L);
		demo.setDecimal(new BigDecimal("84351231246876.576848483123126"));
		demo.setNormalFlag(true);
		
		demo.setDay(new Day("20100826"));
		//demo.setDate(new Date()); //이건 안됨..Date -> JSON의 경우 보여주기용이 출력된다.
	}

}
