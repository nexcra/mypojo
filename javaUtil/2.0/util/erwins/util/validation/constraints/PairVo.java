package erwins.util.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.PairVoValidator;

/** A가 널일경우 B는 널이면 안될때 사용.  즉 2개의 값이 한쌍으로 취급됨 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={PairVoValidator.class})
public @interface PairVo {
	
	String message() default "";
	String start() default "startDate";
	String end() default "endDate";
	String startMsg() default "시작일";
	String endMsg() default "종료일";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
	
}
