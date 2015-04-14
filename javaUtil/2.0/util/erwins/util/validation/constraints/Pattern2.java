package erwins.util.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.Pattern2Validator;

/**
 * 기본 패턴과 유사하나, 치환하고 남은 문자를 msg를 리턴한다. 
 *  */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=Pattern2Validator.class)
public @interface Pattern2 {

	String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String regexp();
}
