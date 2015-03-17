package erwins.util.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import com.google.common.collect.Sets;



public abstract class ValidationUtil{
	
	/** 
	 * 데이터 바인더에서 하는 기본 벨리데이션(Class 미지정) 이외의, 추가 벨리데이션을(Class 지정) 수행해준다.
	 * 캐스팅이 거지같아서 하나 뺐다.
	 *  */
	public static <T> void validateAndThrowIfAble(Validator validator,T target,Class<?> ... classes) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(target,classes);
		if(!constraintViolations.isEmpty()) throw new ConstraintViolationException(Sets.<ConstraintViolation<?>>newHashSet(constraintViolations));
	}
	
	
}
