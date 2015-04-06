package erwins.util.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.MaxByteValidator;

/** 기본적으로 자바(UTF-8)기준으로 한다.  한글3바이트. 영문1바이트
 * 오라클 UTF-8 인코딩일 경우 그대로 사용하면 될듯 하다. */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=MaxByteValidator.class)
public @interface MaxByte {

	String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int value();
	
}
