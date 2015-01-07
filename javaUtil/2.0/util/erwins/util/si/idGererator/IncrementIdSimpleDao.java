package erwins.util.si.idGererator;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

/**
 * 간이 테스트용
 * @author sin
 */
@ThreadSafe
public class IncrementIdSimpleDao implements IncrementIdDao{
    
	private final AtomicLong counter;
	
	public IncrementIdSimpleDao(){
		counter = new AtomicLong();
	}
	
	public IncrementIdSimpleDao(Long init){
		counter = new AtomicLong(init);
	}
	
    public long nextval(){
    	return counter.incrementAndGet();
    }
    
}
