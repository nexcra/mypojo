package erwins.util.spring.web.conversion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 정규식으로 include 하거나 exclude 한다.
 *  */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PatternFormat {
	
	PatternFormatType type() default PatternFormatType.include;
	String regexp();
	
	public static enum PatternFormatType{
		include,
		exclude,
		;
	}
	
}


