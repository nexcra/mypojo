package erwins.util.validation;

import java.text.MessageFormat;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.springframework.core.MethodParameter;
import org.springframework.validation.annotation.Validated;

import com.google.common.collect.Sets;



public abstract class ValidationUtil{
	
	/** 
	 * 데이터 바인더에서 하는 기본 벨리데이션(Class 미지정) 이외의, 추가 벨리데이션을(Class 지정) 수행해준다.
	 * 캐스팅이 거지같아서 하나 뺐다. 포럼에서도 이때문에 무진장 까이는듯??
	 *  */
	public static <T> void validateAndThrowIfAble(Validator validator,T target,Class<?> ... classes) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(target,classes);
		if(!constraintViolations.isEmpty()) throw new ConstraintViolationException(Sets.<ConstraintViolation<?>>newHashSet(constraintViolations));
	}
	
	/** 벨리데이션이 적용된 메소드인지?  */
	public static boolean isValidationMethod(MethodParameter methodParameter){
		if(methodParameter.getParameterAnnotation(Valid.class)!=null) return true;
		if(methodParameter.getParameterAnnotation(Validated.class)!=null) return true;
		return false;
	}
	
	/**  두줄 줄일려고 쓴다.. 이거보다 좋은 방법이 있을거 같다.  */
	public static void replaceViolationText(ConstraintValidatorContext context,String text){
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(text).addConstraintViolation();
	}
	
	/** 간이용도!! 상태가 이상하면 예외를 던진다. */
	public static void check(boolean valid,String pattern,Object ... arguments){
		if(valid) return;
		String errorMsg = MessageFormat.format(pattern, arguments);
		throw new ValidationException(errorMsg);
	}
	
	
}
