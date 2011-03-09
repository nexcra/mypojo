
package erwins.util.counter;


public interface Counter{
    
    /** count를 하나 증가시킨다. */
    public boolean next();
    
    /** 현재 카운터를 리턴한다. */
    public int count();

}