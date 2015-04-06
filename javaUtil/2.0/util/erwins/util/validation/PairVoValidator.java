package erwins.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import erwins.util.lib.ReflectionUtil;
import erwins.util.validation.constraints.PairVo;

public class PairVoValidator implements ConstraintValidator<PairVo,Object>{
	
	public String start ;
	public String end ;

	@Override
	public boolean isValid(Object vo, ConstraintValidatorContext context) {
		if(vo==null) return true;
		Comparable<Object> startValue = ReflectionUtil.findFieldValue(vo, start); 
		Comparable<Object> endValue = ReflectionUtil.findFieldValue(vo, end);
		
		if(startValue==null && endValue==null) return true;
		if(startValue!=null && endValue!=null) return true;
		
		return false ;
	}

	@Override
	public void initialize(PairVo annotation) {
		start  = annotation.start();
		end  = annotation.end();
	}
	
}
