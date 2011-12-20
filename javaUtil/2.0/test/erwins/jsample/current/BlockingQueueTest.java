package erwins.jsample.current;

import java.util.*;
import java.util.concurrent.*;

import erwins.util.nio.*;

public class BlockingQueueTest {

public static void main(String[] args){
        
        //BlockingQueue<String> queue = new ArrayBlockingQueue<String>(Integer.MAX_VALUE);
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        
        ThreadPool pool = new ThreadPool();
        pool.add(new Putter(queue)).setName("Putter-1");
        pool.add(new Putter(queue)).setName("Putter-2");
        pool.add(new Putter(queue)).setName("Putter-3");
        pool.add(new Taker(queue)).setName("Taker-1");
        
        pool.startup();
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            
        }
        pool.shutdown();
    }
    
    public static class Putter extends Thread{
        private final BlockingQueue<String> queue;
        public Putter(BlockingQueue<String> queue){
            this.queue = queue;
        }
        @Override
        public void run() {
            long i=0;
            while(true){
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("합계="+i);
                    return;
                }
                queue.add("asdasd"+i++);
            }
        }
    }
    
    public static class Taker extends Thread{
        private final BlockingQueue<String> queue;
        public Taker(BlockingQueue<String> queue){
            this.queue = queue;
        }
        private static final long size = 10000L;
        @Override
        public void run() {
            List<String> list = new ArrayList<String>();
            try {
                long c = 0;
                while(true){
                    for(long i=0;i<size;i++){
                        String value = queue.take();
                        list.add(value);
                    }
                    System.out.println(size + " * " + ++c + "회 처리");
                    list.clear();
                }
            } catch (InterruptedException e) {
                System.out.println("처리 후 나머지 : "+list.size());
            }
        }
    }
    
    
    
    

}
