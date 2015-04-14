package erwins.util.validation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import erwins.util.lib.CollectionUtil;
import erwins.util.lib.CompareUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.root.exception.PropagatedRuntimeException;
import erwins.util.text.StringUtil;
import erwins.util.validation.constraints.Match;
import erwins.util.validation.constraints.Match.MatchValue;

/** 매칭 비용이...  비싸다 */
public class MatchValidator implements ConstraintValidator<Match,Object>{
	
	private Match annotation;
	
	@Override
	public void initialize(Match annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(Object input, ConstraintValidatorContext context) {
		if(input==null) return true;
		
		//STEP1
		String[] include =  annotation.include();
		if(include.length != 0){
			String text = input instanceof Enum ? ((Enum<?>)input).name() :  input.toString();
			if(Strings.isNullOrEmpty(text)) return true;
			return match(text, context, annotation.include());
		}
		
		//STEP2
		Class<? extends MatchValue> matchClass = CollectionUtil.getFirst(annotation.matchClass());
		if(matchClass!=null){
			MatchValue mv =  ReflectionUtil.newInstance(matchClass);	
			Object[] able = mv.includeValue();
			return match(input, context, able);
		}
		
		//STEP3
		Class<?> target = CollectionUtil.getFirst(annotation.target());
		if(target==null) target = input.getClass();
		String targetName = annotation.targetName();
		try {
			Field field = target.getField(targetName);
			int mod = field.getModifiers();
			Preconditions.checkArgument(Modifier.isStatic(mod),"static required");
			Preconditions.checkArgument(Modifier.isFinal(mod),"final required");
			Object[] able = (Object[]) field.get(null);
			return match(input, context, able);
		} catch (Exception e) {
			throw new PropagatedRuntimeException(e);
		}
	}

	private boolean match(Object input, ConstraintValidatorContext context, Object[] able) {
		boolean pass = CompareUtil.isEqualsAny(input, able);
		if(!pass){
			String enableText = StringUtil.join(able,",");
			ValidationUtil.replaceViolationText(context, enableText);
		}
		return pass;
	}
	
	


	
}
