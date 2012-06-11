package erwins.util.morph;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CollectionOfElements;

import erwins.util.lib.ReflectionUtil;
import erwins.util.lib.CollectionUtil;
import erwins.util.lib.StringUtil;
import erwins.util.morph.anno.Fk;
import erwins.util.root.Singleton;

/**
 * 자료를 복사한다. -> 너무 복잡한듯? 일단 새로 만들자
 */
@Singleton
public class CopyOfDuplicator{
    
    private static CopyOfDuplicator theInstance = new CopyOfDuplicator();
    
    public static CopyOfDuplicator instance(){
        return theInstance;
    }
    
    private List<DuplicatorConfig> list = new CopyOnWriteArrayList<DuplicatorConfig>();

    public CopyOfDuplicator add(DuplicatorConfig command) {
        list.add(command);
        return this;
    }

    /** 결과로 boolean을 반환한다.false이면 복사하지 않는다. */
    public interface DuplicatorConfig {
        public boolean run(String fieldName,Class<?> setterType, Annotation[] annos);
    }
    
    // ===========================================================================================
    //                               protected      
    // ===========================================================================================    
    
    /** target의 정보를 body로 복사한다. 여기서 보통 target은 Request에서 넘어온 사용자의 변경 정보이다. */
    public <T> void shallowCopy(T server,T parameter){
    	new BeanDuplicator<T>().shallowCopy(server,parameter);
    }
    
    private class BeanDuplicator<T>{
        private String fieldName;
        private Annotation[] annos;
        
        public void shallowCopy(T body,T parameter){
            try {
                copy(body,parameter);
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        
        @SuppressWarnings("unchecked")
        private void copy(T body,T parameter) throws Exception{
        	Class<T> clazz = (Class<T>) body.getClass();
        	Method[] methods = clazz.getMethods();
            
            for(Method getter:methods ){
                
            	//getMethods()는 슈퍼클래스의 오버라이드 메소드역시 가져온다. 이는 무시해주자.
                if(getter.isBridge()) continue;
            	
                if(!initGetter(getter)) continue;
                
                if(CollectionUtil.isAnnotationPresent(getter,Id.class,Fk.class)) continue; //키값은 복사하지 않는다.
                if(CollectionUtil.isAnnotationPresent(getter,ManyToOne.class,OneToMany.class,CollectionOfElements.class)) continue; //래퍼런스는 복사안함.
                
                Method setter = ReflectionUtil.toSetter(clazz, fieldName, getter.getReturnType());
                if(setter==null) continue;
				
                if(setter.getParameterTypes().length!=1) continue;
                Class<?> setterType = setter.getParameterTypes()[0]; //bean의 setter의 1번재 parameter를 기준으로 데이터를 검색한다.
                
                //콘피그 설정을 먼저 검사한다.
                boolean pass = true;
                for (DuplicatorConfig each : list) {
                	if(!each.run(fieldName,setterType, annos)) pass = false;
                }
                if(!pass) continue;
                
                Object result = getter.invoke(parameter);
                setter.invoke(body, result);
            }
        }

        /**
         * method의 정보를 추출한다.
         * 해당 조건이 아니면 false를 리턴한다.. 
         */
        private boolean initGetter(Method method) {
            String methodName = method.getName();
            fieldName = StringUtil.getterName(methodName);
            if (fieldName==null) return false;
            if(method.getParameterTypes().length!=0) return false;  //0개의 입력인자만을 인정한다.
            annos = method.getAnnotations();
            return true;
        }
        
    }
    
    
}