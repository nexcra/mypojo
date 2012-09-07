package erwins.jsample;

import java.io.File;
import java.io.IOException;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;


/** 
 * http://joda-time.sourceforge.net/quickstart.html  참고
 * Interval : 1/1000초 단위의 Instant 들 간의 시간 차이. 물론, TimeZone 기반입니다.
 * Duration : TimeZone을 가지지 않는 시간의 길이 (1/1000)단위 Interval로 부터 정보를 얻을 수 있다.
 * Period(기간) TimeZone을 가지지 않는 시간의 길이 (1/1000)단위  특정 필드로 표현된다. 필드 (Period, Years, Months, Weeks, Days, Hours, Minutes, Seconds)
 * 
 * DateTimeFormat 이 스레드 세이프하다 @_@
 * 일자계산 많이해야할때는 MutableDateTime 사용
 *  ISO standard format for datetime, which is yyyy-MM-dd'T'HH:mm:ss.SSSZZ  -> 생성자에 이동할 수 있다.
 *  */ 
public class DigesterExample{
	
	/** 기본기 
	 * @throws SAXException 
	 * @throws IOException */
	//@Test
    public void test() throws IOException, SAXException{
    	Digester digester = new Digester();
        digester.setValidating(true);
        // 최상위 노드를 생성합니다. 객체는 addObjectCreate
        // addObjectCreate(xml 경로와노드명, 객체 타입)
        digester.addObjectCreate("Items", DigesterExample.class);
        // DatabaseInfo를 생성합니다.
        digester.addObjectCreate("Items/Item", DigesterExample.class);
        // DatabaseInfo의 part 지정
        // attributes는 addSetProperties(경로, attribute name, bean 매핑할 property)
        // 메소드로 등록합니다.
        digester.addSetProperties("Items/Item", "SellerID", "itemId");
        digester.addSetProperties("Items/Item", "ItemName", "itemDescrt");
        
        // node 추가, addBeanPropertySetter(경로와이름, 객체필드명)
        /*
        digester.addBeanPropertySetter("config/database/driverclassname", "driverclassname");
        digester.addBeanPropertySetter("config/database/url", "url");
        digester.addBeanPropertySetter("config/database/maxactive", "maxactive");
        digester.addBeanPropertySetter("config/database/maxwait", "maxwait");
        digester.addBeanPropertySetter("config/database/defaultautocommit", "defaultautocommit");
        digester.addBeanPropertySetter("config/database/defaultreadonly", "defaultreadonly");
        digester.addBeanPropertySetter("config/database/defaulttransactionisolation", "defaulttransactionisolation");
        digester.addBeanPropertySetter("config/database/username", "username");
        digester.addBeanPropertySetter("config/database/password", "password");
        */
        // set하기
        // addSetNext(경로와이름, 추가할 메소드명);
        digester.addSetNext("Items/Item", "addItem");
        File input = new File("C:/DATA/download/gmarketItem2012082815/total_100000002.xml");
        DigesterExample list  = (DigesterExample) digester.parse(input);
        System.out.println(list);
    }
	
	
	
	
	
}