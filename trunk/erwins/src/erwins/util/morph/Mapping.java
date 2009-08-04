
package erwins.util.morph;

import java.lang.annotation.*;


/**
 * 영감님 정의 어노테이션
 * resolver에서  component타입을 인식하기 위해서 사용합니다.
 * @author erwins(my.pojo@gmail.com)
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)  //클래스 파일에 저장되고 JVM이 인식한다.
@Target(value=ElementType.METHOD) //method에만 붙일 수 있다.
public @interface Mapping {

    MappingType mappingType() default MappingType.LIST_SUB_ENTITY;
    
    String description() default "";
    
    /**
     * @author     Administrator
     */
    @Deprecated
    public static enum MappingType {
        
        /**
         * FK로 설정되어 ID와 동일시 간주되는것. 
         * 즉 reflection시 ""를 처리할때 0대신 null이 들어와야 하는것이다.
         */
        FK,
        
        /**
         * List에 들어간 ENTITY
         */
        LIST_SUB_ENTITY,
        
        /**
         * String이나 List로 나누어져 들어간것.
         */
        LIST_PARTITIONED_STRING;
        
        public boolean isMatch(Annotation[] annos){
            for(Annotation anno : annos){
                if(anno instanceof Mapping){
                    Mapping map = (Mapping)anno;
                    if(map.mappingType() == this) return true;
                }
            }
            return false;
        }
    }
    
    

}