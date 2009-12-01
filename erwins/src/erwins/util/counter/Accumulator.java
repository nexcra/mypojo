
package erwins.util.counter;

import erwins.util.root.ThreadSafe;


/**
 * 숫자를 누적시키다 초기화 시키는 커뮬레이터.
 */
@ThreadSafe
public class Accumulator extends AccumulatorTemplit{
    
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
    public synchronized boolean next(){
        now++;
        if(now > threshold){
            now = 0;
            if(command!=null) command.run();
            return false;
        }
        return true;
    }

}