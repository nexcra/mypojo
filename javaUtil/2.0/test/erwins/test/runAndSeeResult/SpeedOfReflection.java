
package erwins.test.runAndSeeResult;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import erwins.gsample.GBean;
import erwins.util.morph.Dissolver;
import erwins.util.tools.StopWatch;

/** 리플렉션이 200배 이상 느리다. 하지만 시간은 중요하지 않다. */
public class SpeedOfReflection{

    
    //@Test
    public void reflection() throws Exception {
        System.out.println(StopWatch.load(new Runnable(){
        	public void run() {
        		GBean GBean = new GBean();
        		for(int i=0;i<1000000;i++){
        			GBean.setName("sampleTata");
        		}
        	}
        }));
        System.out.println(StopWatch.load(new Runnable(){
            public void run() {
                GBean GBean = new GBean();
                Class<?> clazz =  GBean.getClass();
                for(int i=0;i<1000000;i++){
                    try {
                        Method m = clazz.getMethod("setTitle",String.class);
                        m.invoke(GBean, "sampleTata");
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }));
    }
    
    /** 약간 무리가 있을 정도의 부하이다. 하지만 큰 문제 없어보인다. 
     * 십만 건에 52초 정도 소요된다. */
    //@Test
    public void reflectionBook() throws Exception {
    	System.out.println(StopWatch.load(new Runnable(){
    		public void run() {
    			for(int i=0;i<100000;i++){
    				GBean GBean = new GBean();
    				GBean.setName("sampleTata");
    				GBean.setCount(i);
    			}
    		}
    	}));
    	
    	System.out.println(StopWatch.load(new Runnable(){
    		public void run() {
    			for(int i=0;i<100000;i++){
    				final Map<String,Object> map = new HashMap<String,Object>(); //Map생성에는 그리 큰 부하가 걸리지 않는다.
    				map.put("name", "sampleTata");
    				map.put("count", i);
    				Dissolver.instance().getBean(map, GBean.class);
    			}
    		}
    	}));
    }
    /** Date()객체의 생성 시간을 알아보자. 거의 안걸리네? */
    @Test
    public void newDate() throws Exception {
    	System.out.println(StopWatch.load(new Runnable(){
    		public void run() {
    			for(int i=0;i<1000000;i++){
    				new Date();
    			}
    		}
    	}));
    }
}
