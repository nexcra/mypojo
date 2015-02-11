
package erwins.util.guava;

import java.lang.annotation.Annotation;

import javax.persistence.ManyToOne;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/** 
 * ManyToOne의 기본구현.
 * @ManyToOne 어노테이션이 있으면 컨버팅하지 않는다.
 *   */
public class ManyToOneStraregy  implements ExclusionStrategy{
	
	@Override
	public boolean shouldSkipField(FieldAttributes arg0) {
		Annotation manyToOne = arg0.getAnnotation(ManyToOne.class); 
		return manyToOne != null;
	}
	
	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}
}