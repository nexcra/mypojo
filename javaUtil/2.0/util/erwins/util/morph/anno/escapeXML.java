
package erwins.util.morph.anno;

import java.lang.annotation.*;


/**
 * JSON 변경시 XML을 escape해준다.
 * getter에만 붙이자.
 * 지금은 디폴트로 되어있어서 안쓴다.. 나중에 성능에 문제가 있다면 바꾸긔~
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface escapeXML {
    String description() default "";
}