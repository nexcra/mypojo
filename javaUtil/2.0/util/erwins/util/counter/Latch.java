
package erwins.util.counter;

import erwins.util.root.NotThreadSafe;





/**
 * 딱 한번만 false로 바뀌는 Latch이다.
 */
@NotThreadSafe
public class Latch{
    
    private boolean first = true;
    
    public boolean first(){
        if(first){
            first = false;
            return true;
        }
        return false;
    }
    

}