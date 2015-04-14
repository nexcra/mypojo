package erwins.util.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.springframework.format.datetime.DateTimeFormatAnnotationFormatterFactory;

import erwins.util.validation.DateStringValidator;

/** 문자열 형식이지만 실제로는 숫자만 들어가는 형태를 컨버팅한다.
 * 실제로는 DateTime을 사용해야 하지만, 낙후된 환경에서 문자열로 DateTime을 대체할때 사용하자
 * @see DateTimeFormatAnnotationFormatterFactory
 * @see @org.springframework.format.annotation.DateTimeFormat(pattern="yyyyMMdd")   */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=DateStringValidator.class)
public @interface DateString {
	
	String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String pattern() default "yyyyMMdd";
	
}
