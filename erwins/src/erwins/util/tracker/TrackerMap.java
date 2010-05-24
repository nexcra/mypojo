
package erwins.util.tracker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ex) 
 *  @Around("execution(public void com.hrd.loms..*Controller.*(..))")
    public void request(ProceedingJoinPoint joinPoint) throws Throwable {
    	long start =  System.nanoTime();
    	joinPoint.proceed();
    	long end =  System.nanoTime();
    	String className  = joinPoint.getTarget().getClass().getName();
    	String methodName = joinPoint.getSignature().getName();
    	map.addTracker(className, methodName, end - start);
    }
 *
 */
public class TrackerMap implements Iterable<Tracker>{
    
	/** 이 map은 외부에서 삭제가 일어나지 않음으로 iterator에 안전하다.  */
    private Map<String,Tracker> map = new HashMap<String,Tracker>();
    
    public void addTracker(String className,String methodName,long time){
    	String key = className + "|" + methodName;
    	Tracker tracker = map.get(key);
    	if(tracker==null){
    		tracker = new Tracker(className,methodName);
    		map.put(key, tracker);
    	}
    	tracker.addTime(time);
    }

	public Iterator<Tracker> iterator() {
		return map.values().iterator();
	}
    
}
