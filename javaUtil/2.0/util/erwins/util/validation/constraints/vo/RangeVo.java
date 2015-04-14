package erwins.util.validation.constraints.vo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.RangeVoValidator;

/** 둘다 값이 존재할경우, 시작값은 종료값보다 작거나 같아야 한다.  */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={RangeVoValidator.class})
public @interface RangeVo {
	
	String message() default "검색기간";
	String start() default "start";
	String end() default "end";
	String startMsg() default "시작";
	String endMsg() default "종료";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
	
}
