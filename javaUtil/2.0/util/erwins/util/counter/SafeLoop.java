
package erwins.util.counter;

import org.apache.commons.lang.mutable.MutableLong;

import erwins.util.root.NotThreadSafe;



/**
 * 무한루프 방지용
 * increment() 호출시 임계치를 넘어가면 예외를 던진다.
 */
@SuppressWarnings("serial")
@NotThreadSafe
public class SafeLoop extends MutableLong{
    
	private final long threshold;
	
    public SafeLoop(long threshold) {
    	this.threshold = threshold;
    }
    
    @Override
    public void increment() {
    	super.increment();
    	if(longValue() > threshold){
        	throw new RuntimeException("too many loop : " + longValue());
        }
    }

}