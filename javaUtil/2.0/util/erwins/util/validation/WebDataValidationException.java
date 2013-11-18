package erwins.util.validation;

import java.util.List;

import javax.validation.ValidationException;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import erwins.util.spring.SpringUtil;


@SuppressWarnings("serial")
public class WebDataValidationException extends ValidationException{
	private final List<FieldError> fieldError;

	/** 반드시 FieldError만 들어와야 한다. 다른게 들어온다면 수정하자. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private WebDataValidationException(List<ObjectError> list) {
		super();
		this.fieldError = (List)list;
	}

	public List<FieldError> getFieldError() {
		return fieldError;
	}
	
	public static void throwExceptionIfHasErrors(BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			throw new WebDataValidationException(bindingResult.getAllErrors());
    	}
	}
	
	/** 기본적인 메세지 구성 샘플이다. 프로젝트별로 수정해서 사용. */
    public String toDefaultString(String separator){
    	List<String> msg = Lists.newArrayList();
    	for(FieldError error :  fieldError){
			msg.add(SpringUtil.elFormat("#{field} --> #{defaultMessage} 입력값 = [#{rejectedValue}],  ", error));
    	}
    	return Joiner.on(separator).join(msg);
    }
	
	
}
