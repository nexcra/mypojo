
package erwins.util.counter;





/**
 * 딱 한번만 false로 바뀌는 Latch이다.
 */
public class Latch implements Counter{
    
    private boolean first = true;
    
    public boolean next(){
        if(first){
            first = false;
            return true;
        }
        return false;
    }
    
    @Override
	public int count() {
    	throw new UnsupportedOperationException();
	}
    

}