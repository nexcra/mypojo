package erwins.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/** 기본적으로 자바(UTF-8)기준으로 한다.  한글3바이트. 영문1바이트
 * 오라클 UTF-8 인코딩일 경우 그대로 사용하면 될듯 하다. */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ByteMaxValidator.class)
public @interface ByteMax {

	String message() default "최대 byte크기({value})를 초과하였습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int value();
	
}
