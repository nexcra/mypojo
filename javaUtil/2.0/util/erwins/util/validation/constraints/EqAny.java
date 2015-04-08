package erwins.util.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.EqAnyValidator;


/** 
 * 입력 가능한 값을 제한함.
 * 입력 가능한 문자나  enum을 제한할때 등에 사용
 *  */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={EqAnyValidator.class})
public @interface  EqAny {

	String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] value();
	
}
