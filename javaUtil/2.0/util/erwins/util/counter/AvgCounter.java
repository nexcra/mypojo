
package erwins.util.counter;

/**
 * 평균 / 최대 / 최소값을 구하는 카운터
 */
public class AvgCounter{
    
    protected long max = 0;
    protected long min = 0;
    protected long count = 0;
    protected long sum = 0;
    protected boolean first = true;
    
    public void add(long value){
    	if(first){
    		max = value;
    		min = value;
    		first = false;
    	}
    	sum+= value;
    	count++;
    	if(max < value) max = value;
    	if(min > value) min = value;
    }
    
    public long getAvarage(){
    	if(sum==0 || count==0) return 0;
    	return sum / count;
    }

	public long getMax() {
		return max;
	}

	public long getMin() {
		return min;
	}

	public long getCount() {
		return count;
	}

	public long getSum() {
		return sum;
	}
    
    
	
}