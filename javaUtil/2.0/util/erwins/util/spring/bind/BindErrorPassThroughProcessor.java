package erwins.util.spring.bind;

import org.springframework.beans.PropertyAccessException;
import org.springframework.validation.BindingErrorProcessor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


/** 
 * 메세지화를 나중에 해주도록 만든 바인딩 에러 처리기.  이게 최선의 방법인지는 모르겠다.
 * 안쓴다.. 나중에 확장 열리면 쓰자
 *  */
@Deprecated
public class BindErrorPassThroughProcessor implements BindingErrorProcessor {

	@Override
	public void processMissingFieldError(String missingField, BindingResult bindingResult) {
		bindingResult.addError(new FieldError("a","b","c"));
	}

	@Override
	public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
		bindingResult.addError(new FieldError("a","b","c"));
	}
	
}
