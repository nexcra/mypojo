package erwins.util.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 문자열 형식이지만 실제로는 숫자만 들어가는 형태를 컨버팅한다.
 * 실제로는 DateMidnight를 사용해야 하지만, 낙후된 환경에서 문자열로 DateTime을 대체할때 사용하자
 * ex) 2013-01-01  ==>  20130101 */
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DigitString {
	
}
