package erwins.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Strings;

import erwins.util.lib.CompareUtil;
import erwins.util.text.StringUtil;
import erwins.util.validation.constraints.EqAny;

public class EqAnyValidator implements ConstraintValidator<EqAny,Object>{
	
	private String[] match;
	
	@Override
	public void initialize(EqAny annotation) {
		match = annotation.value();
	}

	@Override
	public boolean isValid(Object input, ConstraintValidatorContext context) {
		if(input==null) return true;
		String text = input instanceof Enum ? ((Enum<?>)input).name() :  input.toString();
		if(Strings.isNullOrEmpty(text)) return true;
		boolean pass = CompareUtil.isEqualsAny(text, match);
		if(!pass){
			String enableText = StringUtil.join(match,",");
			ValidationUtil.replaceViolationText(context, enableText);
		}
		return pass;
	}
	
	


	
}
