package erwins.util.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.RangeVoValidator;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={RangeVoValidator.class})
public @interface RangeVo {
	
	String message() default "";
	String start() default "startDate";
	String end() default "endDate";
	String startMsg() default "시작일";
	String endMsg() default "종료일";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
	
}
