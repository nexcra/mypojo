package erwins.util.validation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import erwins.util.spring.SpringConversions.PassThroughConverter;
import erwins.util.spring.SpringUtil;
import erwins.util.validation.constraints.DateString;
import erwins.util.validation.constraints.Match;
import erwins.util.validation.constraints.MaxByte;
import erwins.util.validation.constraints.Pattern2;
import erwins.util.validation.constraints.vo.CompositeVo;
import erwins.util.validation.constraints.vo.RangeVo;

public class ConstraintViolationMessageConverter{
    
	private Converter<String,String> fieldNameConverter = new PassThroughConverter<String>();
	private Map<String,String> messageTemplates = Maps.newHashMap();
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/** SpringValidatorAdapter 에서 복붙했다.. 맘에 안들어..  리플렉션 시 기본 어노테이션 인자를 제거한다. */
	private static final Set<String> internalAnnotationAttributes = new HashSet<String>(3);

	static {
		internalAnnotationAttributes.add("message");
		internalAnnotationAttributes.add("groups");
		internalAnnotationAttributes.add("payload");
	}
	
	public String convert(WebDataValidationException e,String separator){
		List<String> msg = Lists.newArrayList();
    	for(FieldError error :  e.getFieldError()) msg.add(convert(error));
    	return Joiner.on(separator).join(msg);
	}
	
	public String convert(ConstraintViolationException e,String separator){
		List<String> msg = Lists.newArrayList();
		for(ConstraintViolation<?> violation : e.getConstraintViolations() ){
			msg.add(convert(violation));
		}
    	return Joiner.on(separator).join(msg);
	}
	
	/** 스프링의 필드 에러. 어차피 이건 ConstraintViolation에서 변형되는거다.. 왜이따위로 되있는지는 의문. 나중에 사라질듯.. */
	public String convert(ObjectError error){
		ConstraintViolationBean bean = new ConstraintViolationBean();
		bean.setCode(error.getCode());
		bean.setMsg(error.getDefaultMessage());
		bean.setArgs(error.getArguments());
		if(error instanceof FieldError){
			FieldError fieldError = (FieldError) error;
			bean.setField(fieldError.getField());
			bean.setFieldName(fieldNameConverter.convert(bean.getField()));
			bean.setRejectedValue(fieldError.getRejectedValue());
		}
		return convert(bean);
	}
	
	/** 자바 표준의 제약조건위반 */
	public String convert(ConstraintViolation<?> violation){
		ConstraintViolationBean bean = new ConstraintViolationBean();
		Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
		bean.setCode(annotation.annotationType().getSimpleName()); // Proxy 임으로  annotation.getClass().getSimpleName() 하면 안된다.
		bean.setMsg(violation.getMessage()); //??
		
		//이하 Args 추출구간 복붙했다.
		List<Object> arguments = new LinkedList<Object>();
		arguments.add(""); //ObjectError와 맞춰주기 위해 첫인자는 무시한다.
		Map<String, Object> attributesToExpose = new TreeMap<String, Object>();
		for (Map.Entry<String, Object> entry : violation.getConstraintDescriptor().getAttributes().entrySet()) {
			String attributeName = entry.getKey();
			Object attributeValue = entry.getValue();
			if (!internalAnnotationAttributes.contains(attributeName)) {
				attributesToExpose.put(attributeName, attributeValue);
			}
		}
		arguments.addAll(attributesToExpose.values());
		bean.setArgs(arguments.toArray(new Object[arguments.size()]));
		bean.setField(violation.getPropertyPath().toString());
		bean.setFieldName(fieldNameConverter.convert(bean.getField()));
		bean.setRejectedValue(violation.getInvalidValue());
		return convert(bean);
	}
	
	/** 범용으로 사용하기 위해 코드가 String이다  */
	private String convert(ConstraintViolationBean bean){
		String messageTemplate = messageTemplates.get(bean.getCode());
		if(messageTemplate==null) {
			log.debug(bean.toString());
			return bean.getMsg();
		}
		return SpringUtil.elFormat(messageTemplate, bean);
	}
	
	public <T extends Annotation> void add(Class<T> validation,String message){
		add(validation.getSimpleName(), message);
	}
	public <T extends Annotation> void add(String code,String message){
		messageTemplates.put(code, message);
	}
	
	/** 이 내용은 어떤 벨리데이터가 있는지 참고용으로도 사용한다. */
	public ConstraintViolationMessageConverter(){
		//입력실패
		add("typeMismatch","[#{fieldName}] : '#{rejectedValue}' <- 적합한 입력 형식이 아닙니다");
		//기본
		add(NotEmpty.class,"[#{fieldName}] : 필수입력항목입니다");
		add(NotNull.class,"[#{fieldName}] : 필수입력항목입니다");
		add(Pattern.class,"[#{fieldName}] : '#{rejectedValue}' <- 입력 불가능한 문자열이 포함되어 있습니다");
		add(Pattern2.class,"[#{fieldName}] : '#{rejectedValue}' <- 입력 불가능한 문자열(#{msg})이 포함되어 있습니다");
		add(Length.class,"[#{fieldName}] : '#{rejectedValue}' <- 입력값의 길이는 제한조건(#{args[2]}~#{args[1]}) 사이여야 합니다");
		add(MaxByte.class,"[#{fieldName}] : '#{rejectedValue}' <- 최대 입력 바이트수(#{args[1]}byte)를 초과하였습니다");
		add(Size.class,"[#{fieldName}] : '#{rejectedValue}' <- 입력값의 길이는 제한조건인 #{args[2]}~#{args[1]} 사이여야 합니다"); //컬렉션도 되는애.. 별로임
		add(Match.class,"[#{fieldName}] : '#{rejectedValue}' <- 입력가능한 값들(#{msg})이 아닙니다");
		//기본-숫자
		add(Max.class,"[#{fieldName}] : '#{rejectedValue}' <- 최대 입력값(#{args[1]}) 미만으로 입력해 주세요");
		add(Min.class,"[#{fieldName}] : '#{rejectedValue}' <- 최소 입력값(#{args[1]}) 이상으로 입력해 주세요");
		add(DecimalMax.class,"[#{fieldName}] : '#{rejectedValue}' <- 최대 입력값(#{args[1]}) 미만으로 입력해 주세요");
		add(DecimalMin.class,"[#{fieldName}] : '#{rejectedValue}' <- 최소 입력값(#{args[1]}) 이상으로 입력해 주세요");
		add(Range.class,"[#{fieldName}] : '#{rejectedValue}' <- 입력값의 크기는 제한조건인(#{args[2]}~#{args[1]}) 사이여야 합니다");
		//커스텀
		add(Email.class,"[#{fieldName}] : '#{rejectedValue}' <- 적합한 이메일 형식이 아닙니다");
		add(URL.class,"[#{fieldName}] : '#{rejectedValue}' <- 적합한 URL 형식이 아닙니다");
		add(CreditCardNumber.class,"[#{fieldName}] : '#{rejectedValue}' <- 적합한 신용카드 번호가 아닙니다");
		add(DateString.class,"[#{fieldName}] : '#{rejectedValue}' <- 작합한 일자 형식(#{args[1]})에 적합하지 않습니다");
		add(ScriptAssert.class,"#{msg}"); //애는 답이없다. 참고만 하던가 알아서 쓰자.  ex) @ScriptAssert(lang = "javascript", script = "_this.startDate > _this.endDate")
		//커스텀-클래스
		add(RangeVo.class,"[#{msg}] <- #{args[4]}은 #{args[2]}보다 작거나 같아야 합니다");
		add(CompositeVo.class,"[#{msg}] <- 모두 비어있거나 모두 입력되어야 합니다");
	}
	
	
	/** 결과메세지를 만들기 위한 템플릿용 빈 */
	@Data
	private static class ConstraintViolationBean{
		private String code;
		/** 디폴트 메세지. 커스터마이징된거 포함 */
		private String msg;
		/** key를 사용한 TreeMap 순으로 정렬된다. */
		private Object[] args;
		private String field;
		private String fieldName;
		private Object rejectedValue;
	}


	public Converter<String, String> getFieldNameConverter() {
		return fieldNameConverter;
	}

	public void setFieldNameConverter(Converter<String, String> fieldNameConverter) {
		this.fieldNameConverter = fieldNameConverter;
	}
	
	
}
