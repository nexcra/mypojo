
package erwins.util.morph.anno;

import java.lang.annotation.*;


/**
 * 오라클의 VChar4000제한 때문에 String이나 List로 나누어져 들어간것.
 * setter에만 붙이면 될듯 하다.
 * @author erwins(my.pojo@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)  //클래스 파일에 저장되고 JVM이 인식한다.
@Target(value=ElementType.METHOD) //method에만 붙일 수 있다.
public @interface OracleListString {
    String description() default "";
    
}