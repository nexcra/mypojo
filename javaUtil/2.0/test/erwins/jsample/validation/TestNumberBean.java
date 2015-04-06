package erwins.jsample.validation;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.Data;


@Data
public class TestNumberBean{
	
	
	@NotNull
	private Long n01;
	@Max(20)
	private Long n02;
	
	@DecimalMax("30.24")
	@DecimalMin("19.57")
	private BigDecimal n03;
	
	@Max(100)
	private BigDecimal n04;
	
	@Range(min=0,max=50)
	private BigDecimal n05;
	
	
}
