
package erwins.util.guava;

import java.lang.annotation.Annotation;

import javax.persistence.OneToMany;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/** oneToMany의 기본구현.  */
public class OneToManyStraregy  implements ExclusionStrategy{
	
	@Override
	public boolean shouldSkipField(FieldAttributes arg0) {
		Annotation oneToMany = arg0.getAnnotation(OneToMany.class); 
		return oneToMany != null;
	}
	
	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}
}