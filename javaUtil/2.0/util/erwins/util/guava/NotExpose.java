
package erwins.util.guava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Google Guava의 Expose와 유사.  
 * ex) 
 * gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes arg0) {
				Annotation notExpose = arg0.getAnnotation(NotExpose.class); 
				return notExpose != null;
			}
			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				return false;
			}
		});
		아래는 덤.
		gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC,Modifier.TRANSIENT); 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface NotExpose {
	
}