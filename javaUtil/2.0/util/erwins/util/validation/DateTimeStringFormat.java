package erwins.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/** 문자열 형식이지만 실제로는 숫자만 들어가는 형태를 컨버팅한다.
 * 실제로는 DateTime을 사용해야 하지만, 낙후된 환경에서 문자열로 DateTime을 대체할때 사용하자
 * @see DateTimeFormatAnnotationFormatterFactory
 * @see @org.springframework.format.annotation.DateTimeFormat(pattern="yyyyMMdd")   */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=DateTimeStringFormatValidator.class)
public @interface DateTimeStringFormat {
	
	String message() default "[{0}] 적합한 형식이 아닙니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String pattern() default "yyyyMMdd";
	
}
