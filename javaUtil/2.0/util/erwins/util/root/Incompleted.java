package erwins.util.root;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 수정할점이 있거나 맘에 안들거나 뭔가 찜찜한 소스코드에 붙인다.
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Incompleted {

}
