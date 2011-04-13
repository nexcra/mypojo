
package erwins.jsample.current;

import org.junit.Test;

public class ThreadSafeSample {

    /**
     * @throws Exception
     */
    @Test
    public void toStr() throws Exception {
        SecurityGate o = new SecurityGate();
        new Th(o).start();
        new Th(o).start();
    }
    
    /**
     * @author  Administrator
     */
    public static class Th extends Thread {
        /**
         * @uml.property  name="o"
         * @uml.associationEnd  
         */
        SecurityGate o;
        public Th(SecurityGate o){
            this.o = o;
        }
        @Override
        public void run(){
            for(int i=0;i<1000;i++){
                o.enter();
                o.exit();
                if(o.getCounter()!=0) System.out.println("zz");
            }
             
            System.out.println(o.getCounter());
        }
        
    }

    /**
     * @author  Administrator
     */
    public static class SecurityGate {
        /**
         * @uml.property  name="counter"
         */
        private int counter = 0;

        public void enter() {
            int currentCounter = counter;            Thread.yield();
            counter = currentCounter + 1;
        }

        public void exit() {
            int currentCounter = counter;
            Thread.yield();
            counter = currentCounter - 1;
        }

        /**
         * @return
         * @uml.property  name="counter"
         */
        public int getCounter() {
            return counter;
        }
    }

}
