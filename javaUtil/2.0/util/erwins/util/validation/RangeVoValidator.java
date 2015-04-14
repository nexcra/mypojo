package erwins.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import erwins.util.lib.ReflectionUtil;
import erwins.util.validation.constraints.vo.RangeVo;

public class RangeVoValidator implements ConstraintValidator<RangeVo,Object>{
	
	public String start ;
	public String end ;

	@Override
	public boolean isValid(Object vo, ConstraintValidatorContext context) {
		if(vo==null) return true;
		Comparable<Object> startValue = ReflectionUtil.findFieldValue(vo, start); 
		Comparable<Object> endValue = ReflectionUtil.findFieldValue(vo, end);
		
		if(startValue==null) return true;
		if(endValue==null) return true;
		
		boolean  isValid = startValue.compareTo(endValue) <= 0;
//		if(!isValid){
//			context.disableDefaultConstraintViolation();
//			context.buildConstraintViolationWithTemplate(startValue + " ~ " + endValue).addConstraintViolation();
//		}
		return isValid ;
	}

	@Override
	public void initialize(RangeVo annotation) {
		start  = annotation.start();
		end  = annotation.end();
	}
	
}
