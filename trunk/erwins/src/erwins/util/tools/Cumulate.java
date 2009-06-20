
package erwins.util.tools;

import erwins.util.root.ThreadSafe;


/**
 * 숫자를 누적시키다 초기화 시키는 커뮬레이터.
 */
@ThreadSafe
public class Cumulate{
    
    private int threshold;
    private int now = 0;
    
    public Cumulate(int threshold){
        this.threshold = threshold;
    }
    
    /**
     * 임계치를 넘어가면 카운트를 초기화하고 false를 리턴한다. 
     */
    public synchronized boolean next(){
        now++;
        if(now > threshold){
            now = 0;
            return false;
        }else return true;
    }

}