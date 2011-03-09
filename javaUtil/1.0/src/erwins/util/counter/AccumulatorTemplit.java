
package erwins.util.counter;



/**
 * 숫자를 누적시키다 초기화 시키는 커뮬레이터.
 */
public abstract class AccumulatorTemplit implements Counter{
    
    protected int threshold;
    protected int now = 0;
    protected final Runnable command;
    
    public AccumulatorTemplit(int threshold){
        this.threshold = threshold;
        this.command = null;
    }
    public AccumulatorTemplit(int threshold,Runnable command){
        this.threshold = threshold;
        this.command = command;
    }

}