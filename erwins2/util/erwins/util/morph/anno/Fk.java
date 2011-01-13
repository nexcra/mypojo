
package erwins.util.morph.anno;

import java.lang.annotation.*;


/**
 * FK로 설정되어 ID와 동일시 간주되는것. 
 * 즉 reflection시 ""를 처리할때 0대신 null이 들어와야 하는것이다.
 * 일단 사용 안함.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface Fk {
    String description() default "";
    
}