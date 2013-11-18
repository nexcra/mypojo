package erwins.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/** 직접 파싱하는 방법을 사용함으로, 무거운 로직에는 사용하면 안된다. */
public class DateTimeStringFormatValidator implements ConstraintValidator<DateTimeStringFormat,String>{
	
	public DateTimeFormatter pattern ;

	@Override
	public boolean isValid(String text, ConstraintValidatorContext arg1) {
		if(text==null) return true;
		try {
			pattern.parseDateTime(text);
		} catch (Exception e) {
			return false;	
		}
		return true;
	}

	@Override
	public void initialize(DateTimeStringFormat annotation) {
		pattern = DateTimeFormat.forPattern(annotation.pattern());
	}
	
}
