
package erwins.util.tools;

import org.junit.Test;

public class StopWatchTest {

    @Test
    public void test() throws InterruptedException{
    	StopWatch s = new StopWatch();
    	
    	s.check("a");
    	Thread.sleep(1534);
    	s.check("bb");
    	Thread.sleep(387);
    	s.check("ccc");
    	Thread.sleep(101);
    	
    	System.out.println(s);
    }
    
    

}