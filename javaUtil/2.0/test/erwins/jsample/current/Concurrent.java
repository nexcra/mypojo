package erwins.jsample.current;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class Concurrent{

    final AtomicReference<Integer> c = new AtomicReference<Integer>(0);
    
    /**
     * 자바의 래퍼런스 관계
     */
    @Test
    public void reference(){
        c.set(c.get()+1) ;
        System.out.println(c);
        c.set(c.get()+1) ;
        System.out.println(c);
    }
    
    

}
