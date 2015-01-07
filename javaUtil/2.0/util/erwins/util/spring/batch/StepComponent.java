package erwins.util.spring.batch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** 같단 어노테이션 .  스텝 스코프에 문자열이 들어가기때문에 따로 만들었다. */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Component
@Scope("step")
public @interface StepComponent {
	
}
