package erwins.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import erwins.util.lib.CompareUtil;
import erwins.util.lib.ReflectionUtil;
import erwins.util.validation.constraints.vo.CompositeVo;

public class CompositeValueValidator implements ConstraintValidator<CompositeVo,Object>{
	
	public String[] fieldNames ;

	@Override
	public boolean isValid(Object vo, ConstraintValidatorContext context) {
		if(vo==null) return true;
		Multiset<Boolean> set = HashMultiset.create();
		for(String fieldName : fieldNames){
			Object value = ReflectionUtil.findFieldValue(vo, fieldName);
			boolean isEmpty = CompareUtil.isEmptyObject(value);
			set.add(isEmpty);
		}
		return set.elementSet().size() == 1 ;
	}

	@Override
	public void initialize(CompositeVo annotation) {
		fieldNames  = annotation.fieldName();
	}
	
}
