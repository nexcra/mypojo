package erwins.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import erwins.util.validation.constraints.MaxByte;

public class MaxByteValidator implements ConstraintValidator<MaxByte,String>{
	
	public int  maxByteSize ;

	@Override
	public boolean isValid(String text, ConstraintValidatorContext arg1) {
		if(text==null) return true;
		if(text.getBytes().length > maxByteSize) return false; 
		return true;
	}

	@Override
	public void initialize(MaxByte annotation) {
		maxByteSize = annotation.value();
	}
	
}
