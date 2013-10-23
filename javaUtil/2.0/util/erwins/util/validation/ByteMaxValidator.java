package erwins.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ByteMaxValidator implements ConstraintValidator<ByteMax,String>{
	
	public int  maxByteSize ;

	@Override
	public boolean isValid(String text, ConstraintValidatorContext arg1) {
		if(text==null) return true;
		if(text.getBytes().length > maxByteSize) return false; 
		return true;
	}

	@Override
	public void initialize(ByteMax annotation) {
		maxByteSize = annotation.value();
	}
	
}
