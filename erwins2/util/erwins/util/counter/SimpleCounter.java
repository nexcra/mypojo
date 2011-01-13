
package erwins.util.counter;

/** final int를 대체할때 사용된다. */
public class SimpleCounter implements Counter{
    
    protected int count = 0;

	@Override
	public int count() {
		return count;
	}

	@Override
	public boolean next() {
		count++;
		return true;
	}
    
    
    
    
	
}