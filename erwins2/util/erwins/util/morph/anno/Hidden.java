
package erwins.util.morph.anno;

import java.lang.annotation.*;


/**
 * 비밀번호 등의 view에서 보여지면 안되는 것들.
 * json등에서 걸러진다.
 * @author erwins(my.pojo@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)  //클래스 파일에 저장되고 JVM이 인식한다.
@Target(value=ElementType.METHOD) //method에만 붙일 수 있다.
public @interface Hidden {

    String description() default "";
    
}