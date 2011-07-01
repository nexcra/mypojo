
package erwins.util.root;

import java.lang.annotation.*;


/**
 * Singleton임을 나타낸다. 
 * GoF식 Singleton 또는 Spring DI로 활용하자. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface Singleton {

}