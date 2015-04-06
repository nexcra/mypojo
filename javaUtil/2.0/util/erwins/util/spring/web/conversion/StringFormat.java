package erwins.util.spring.web.conversion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.format.Parser;

/**
 * 각종 문자열을 변환한다. 순서대로 적용된다.
 *  */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringFormat {
	
	Class<? extends Parser<String>>[] value();
	
}


