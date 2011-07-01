
package erwins.util.morph.anno;

import java.lang.annotation.*;


/**
 * String이지만 숫자만 받아야 하는 setter에 붙이자.
 * ex)  날자 / 민번 / HS_CODE 등 
 * reflection시 숫자만 남기고 모두 삭제해준다.
 */
@Retention(RetentionPolicy.RUNTIME)  //클래스 파일에 저장되고 JVM이 인식한다.
@Target(value=ElementType.METHOD) //method에만 붙일 수 있다.
public @interface Numeric {

    String description() default "";
    
}