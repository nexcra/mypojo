package erwins.util.validation.constraints.vo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.CompositeValueValidator;

/** 
 * 해당 필드의 값이 모두 널이거나, 모두 널이 아니거나.
 * message 꼭 넣도록 하자
 * */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={CompositeValueValidator.class})
public @interface CompositeVo {
	
	String message() default "시작/종료";
	String[] fieldName() default {"start","end"};
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
	
}
