
package erwins.util.guava;

import java.lang.annotation.Annotation;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/** NotExpose의 기본구현.  */
public class NotExposeStrategy  implements ExclusionStrategy{
	
	@Override
	public boolean shouldSkipField(FieldAttributes arg0) {
		Annotation notExpose = arg0.getAnnotation(NotExpose.class); 
		return notExpose != null;
	}
	
	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}
}