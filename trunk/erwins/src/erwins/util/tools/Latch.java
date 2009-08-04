
package erwins.util.tools;



/**
 * 딱 한번만 false로 바뀌는 Latch이다.
 */
public class Latch{
    
    private boolean first = true;
    
    public boolean isFirst(){
        if(first){
            first = false;
            return true;
        }
        return first;
    }
    

}