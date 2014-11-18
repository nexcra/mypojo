package erwins.util.spring;

import java.util.List;

import lombok.Data;
import lombok.Delegate;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.google.common.collect.Lists;



@Data
public class FlatBindingResult implements BindingResult{
	
	/** 이거 새로 추가할라고 이짓을 했다. */
	public List<FlatFieldError> getAllFlatFieldErrors() {
		List<FlatFieldError> errors = Lists.newArrayList();
		for(ObjectError each : getAllErrors()){
			FieldError fieldError = (FieldError) each;
    		String fieldName = fieldError.getField();
    		LineMetadata lineMetadata = flatDataBinder.getLineMetadata(fieldName);
    		FlatFieldError flatError = new FlatFieldError(fieldError, lineMetadata);
    		errors.add(flatError);
		}
		return errors;
	}
	
	/** 위임인자 */
	@Delegate
	private final BindingResult bindingResult;
	private final FlatDataBinder<?> flatDataBinder;

}
