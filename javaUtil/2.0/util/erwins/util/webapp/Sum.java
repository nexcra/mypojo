
package erwins.util.webapp;

import java.io.Serializable;
import java.math.BigDecimal;

import erwins.util.text.StringUtil;



/** 빅데시발 ㅅㅂ땜에 만듬. 불변객체 아님으로 래퍼런스 넘기기 가능. */
@SuppressWarnings("serial")
public class Sum implements Serializable{
	
	private BigDecimal decimal = BigDecimal.ZERO;
	
	public void add(BigDecimal add){
		decimal = decimal.add(add);
	}
	public void add(String add){
		if(StringUtil.isEmpty(add)) return;
		decimal = decimal.add(new BigDecimal(add));
	}
	public BigDecimal getDecimal() {
		return decimal;
	}
	
}
