package erwins.jsample.validation;

import java.util.Date;

import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;

import lombok.Data;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import erwins.jsample.validation.TestEnum.InputA;
import erwins.util.spring.web.conversion.PatternFormat;
import erwins.util.spring.web.conversion.StringFormat;
import erwins.util.spring.web.conversion.TrimParser;
import erwins.util.validation.constraints.DateString;
import erwins.util.validation.constraints.Match;
import erwins.util.validation.constraints.MaxByte;
import erwins.util.validation.constraints.Pattern2;
import erwins.util.validation.constraints.vo.CompositeVo;

//@ScriptAssert(lang = "javascript", script = "_this.startDate > _this.endDate")
@Data
@CompositeVo(fieldName={"startDate","endDate"})
public class TestStringBean{
	
	@PatternFormat(regexp="[0-9]*")
	@Length(min=4,max=8)
	private String s01;
	
	@DateString
	@PatternFormat(regexp="[0-9]")
	private String s02;
	
	@DateString(pattern="yyyy-MM-dd HH:mm")
	private String s03;
	
	@DateString
	private String s04;
	
	@MaxByte(10)
	private String s05;
	
	@Pattern(regexp="[abc]*")
	private String s06;
	
	@Email
	@StringFormat(TrimParser.class)
	private String email;
	
	@URL
	private String url;
	
	@CreditCardNumber(groups=TestString01Group.class)
	private String cno;
	
	private String startDate;
	private Date endDate;
	
	private Long num;
	
	@Pattern2(regexp="ㄱ-ㅎㅏ-ㅣ가-힣-a-zA-Z0-8")
	private String s07;
	
	@PatternFormat(regexp="[A-Z0-8]")
	private String s08;
	
	@Match(include={"A","B","CC"})
	private String s09;
	
	@Match(include={"EN01","EN03"})
	private TestEnum s10;
	
	@Match(targetName="INPUT_A")
	private TestEnum s11;
	
	@Match(matchClass=InputA.class)
	private TestEnum s12;
	
	public static interface TestString01Group extends Default{
		
	}
	
	public static interface TestString02Group extends TestString01Group{
		
	}
	

}
