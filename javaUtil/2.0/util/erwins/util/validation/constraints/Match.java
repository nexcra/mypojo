package erwins.util.validation.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import erwins.util.validation.MatchValidator;


/** 
 * 입력 가능한 문자나  enum을 제한할때 등에 사용
 * static final 로 지정해도 되고, 문자열로 입력해도 된다.
 * enum 파라메터가 컴파일 시점에서만 적용되기때문에 이런 방법을 사용했다.
 *  */
@Target({ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={MatchValidator.class})
public @interface  Match {

	String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    /** STEP1 */
    String[] include() default {}; //문자열로 매치할때 쓴다. 이게 있으면 이걸 우선함
    
    /** STEP2 */
    Class<? extends MatchValue>[] matchClass() default {}; //컴파일 타임에 알 수 있는 마커
    
    /** STEP3 */
    Class<?>[] target() default {};  // static final 로 정의된 상수가 있는 클래스. 없으면 대상 클래스로(주로 enum) 사용 
    String targetName() default "";  // static final 로 지정된 필드 이름
    
    public static interface MatchValue{
    	public Object[] includeValue();
    }
	
}
