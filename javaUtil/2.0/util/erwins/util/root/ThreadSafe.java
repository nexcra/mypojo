package erwins.util.root;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 기존 스래스세이프, 낫세이프가 jar별로 틀려서 하나 만들었다. 나중에 jar가 합쳐진다면 삭제후 javax를 사용 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface ThreadSafe {

}
