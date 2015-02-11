package erwins.util.si.idGererator;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

/**
 * 간이 테스트용
 * @author sin
 */
@ThreadSafe
public class IncrementIdSimpleRepository implements IncrementIdRepository{
    
	private final AtomicLong counter;
	
	public IncrementIdSimpleRepository(){
		counter = new AtomicLong();
	}
	
	public IncrementIdSimpleRepository(Long init){
		counter = new AtomicLong(init);
	}
	
    public long nextval(){
    	return counter.incrementAndGet();
    }
    
}
