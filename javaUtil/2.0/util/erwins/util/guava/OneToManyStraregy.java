
package erwins.util.guava;

import java.lang.annotation.Annotation;

import javax.persistence.OneToMany;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/** oneToMany의 기본구현.
 * @OneToMany 어노테이션이 있으면 컨버팅하지 않는다.  */
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