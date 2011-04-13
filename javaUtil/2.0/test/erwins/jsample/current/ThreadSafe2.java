
package erwins.jsample.current;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSafe2 {
    
    public static void main(String[] args){
        System.out.println("start");
        ThreadSafe2 qwe= new  ThreadSafe2();
        qwe.readAndWrite();
    }

    public void readAndWrite(){
        ExecutorService s = Executors.newFixedThreadPool(3);
        
        final int qq = 0;
        
        for(int i=0;i<10;i++){
            Runnable r = new Runnable(){
                public void run() {
                    System.out.println(qq);
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        
                    }
                    System.out.println("end");
                }
            };
            s.execute(r);
            //Thread.sleep(6000);
            //s.shutdown();
        }
        
    }
}
