package erwins.util.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import erwins.util.validation.constraints.Pattern2;

public class Pattern2Validator implements ConstraintValidator<Pattern2,String>{
	
	private Pattern pattern;
	
	@Override
	public void initialize(Pattern2 annotation) {
		String  regexp = annotation.regexp();
		if(!regexp.startsWith("[")) regexp = "["+regexp+"]*";
		pattern = Pattern.compile(regexp);
	}

	@Override
	public boolean isValid(String text, ConstraintValidatorContext context) {
		if(text==null) return true;
		
		Matcher m = pattern.matcher(text);
		if(!m.matches()){
			String excludeText = m.replaceAll("");
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(excludeText).addConstraintViolation();
			return false;
		}
		
		return true;
	}
	
	


	
}
