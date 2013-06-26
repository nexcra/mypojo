package erwins.util.hibernate;

import java.lang.reflect.Method;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CollectionOfElements;

import erwins.util.lib.ReflectionUtil;

/**
 * 하이버네이트 jar에 종속적인 유틸 모음.
 */
public abstract class HiberUtil{
    
    /**
     * 객체의 1차 연관객체를 모두 로딩한다.
     * 아오 복잡해.. 쓰지 말자.
     */
    @SuppressWarnings("unchecked")
    public static void initializeAll(Object obj){
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        
        for(Method method : methods){
            //if(!ReflectionUtil.isGetter(method)) continue;
            if(ReflectionUtil.isAnnotationPresent(method, CollectionOfElements.class,OneToMany.class,ManyToOne.class)){
                if(method.getParameterTypes().length!=0) continue;
                Object proxy;
                try {
                    proxy = method.invoke(obj);
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
                Hibernate.initialize(proxy);
            }
        }
    }
}
