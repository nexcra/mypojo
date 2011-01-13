
package erwins.util.counter;



/**
 * 숫자를 누적시키다 초기화 시키는 커뮬레이터.
 */
public class Accumulator extends AccumulatorTemplit{
	
	private ThreashHoldRun threashHoldRun;
	private int count = 0;
	
	public Accumulator(int threshold, ThreashHoldRun threashHoldRun) {
		super(threshold);
        this.threashHoldRun = threashHoldRun;
    }
	
	public static interface ThreashHoldRun{
		public void run(int count);
	}
    
    public Accumulator(int threshold, Runnable command) {
        super(threshold, command);
    }

    public Accumulator(int threshold) {
        super(threshold);
    }

    /**
     * 임계치를 넘어가면 카운트를 초기화하고 false를 리턴한다. 
     * command가 있으면 실행시킨다.
     */
    public boolean next(){
        now++;
        if(now >= threshold){
            now = 0;
            count++;
            if(command!=null) command.run();
            if(threashHoldRun!=null) threashHoldRun.run(count);
            return false;
        }
        return true;
    }
    
    @Override
	public int count() {
		return now;
	}    

}