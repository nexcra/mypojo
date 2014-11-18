package erwins.util.spring;

import java.text.MessageFormat;

import lombok.Data;
import lombok.Delegate;

import org.springframework.validation.FieldError;

import erwins.util.text.StringUtil;


/** ObjectError가 부실해서 하나 만들었다. */
@Data
public class FlatFieldError {

	@Delegate
	private final FieldError fieldError;
	private final LineMetadata lineMetadata;
	/** 커스터마이징된 메세지를 여기에 담자 */
	private String message;
	
	/** 
	 * 샘플입니다. 각 플젝마다 만들어 쓰세요
	 * 등록된게 없으면 null
	 *  */
	public static String toDefaultMessage(FieldError each){
		String code = each.getCode(); 
		if(code.equals("typeMismatch")){
			return  MessageFormat.format("[{0}] <-- 적합한 형식의 입력값이 아닙니다.",each.getRejectedValue());	
		}
		if(StringUtil.isEquals(code, "NotEmpty","NotNull","required")){ //어노테이션 2종 && 스프링바인더의 setRequiredFields 옵션
			return  "필수입력 항목이 누락되었습니다.";	
		}
		return null;
	}
	

}
