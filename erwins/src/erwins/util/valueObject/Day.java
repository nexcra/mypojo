package erwins.util.valueObject;

import erwins.util.lib.Strings;


/**
 * yyyyMMdd 스타일의 8자리 일자를 나타낸다. 불변객체 아님. ㅠㅠ
 */
public class Day implements ValueObject{
    
    String day;
    String month;
    String year;
        
    @Override
    public String toString(){
        return year+"년"+month+"월"+day+"일";
    }

	@Override
	public Object getValue() {
		return year + month + day;
	}

	@Override
	public void setValue(Object obj) {
		String yyyyMMdd = Strings.getNumericStr(obj);
    	if(yyyyMMdd.length()!=8) throw new RuntimeException(yyyyMMdd + " : day lenth must be 8!");
    	year = yyyyMMdd.substring(0,4);
    	month = yyyyMMdd.substring(5,6);
    	day = yyyyMMdd.substring(7,8);
	}

	public String getDay() {
		return day;
	}

	public String getMonth() {
		return month;
	}

	public String getYear() {
		return year;
	}
	
	
}