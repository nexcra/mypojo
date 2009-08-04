
package erwins.util.tools;

import erwins.util.root.ThreadSafe;


/**
 * 숫자를 누적시키다 초기화 시키는 커뮬레이터.
 */
@ThreadSafe
public class Accumulator{
    
    private int threshold;
    private int now = 0;
    private final Runnable command;
    
    public Accumulator(int threshold){
        this.threshold = threshold;
        this.command = null;
    }
    public Accumulator(int threshold,Runnable command){
        this.threshold = threshold;
        this.command = command;
    }
    
    /**
     * 항상 true를 리턴한다.
     * 임계치를 넘어가면 예외를 던진다.
     * 무한루프 방지용으로 사용한다.
     */
    public boolean loop(){
        now++;
        if(now > threshold) throw new RuntimeException(now + " is too many loop");
        return true;
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