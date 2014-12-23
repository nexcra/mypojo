
package erwins.util.guava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Google Guava의 Expose와 유사.  
 * ex) 
 * gsonBuilder.setExclusionStrategies(new NotExposeStrategy());
		아래는 덤.
		gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC,Modifier.TRANSIENT); 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface NotExpose {
	String value() default "";
}