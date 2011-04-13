package erwins.jsample;

import org.junit.Test;

import erwins.util.exception.ExceptionUtil;


/**
 * 실글톤이 셧다운될때 가동되는 스래드.
 * 프로그램이 비정상적으로 종료되면 훅이 작동하지 않으니 주의!
 * 훅을 여러개 등록할 경우 무작위로 동시에 실행된다.
 * 주의!! Hibernate등의 프레임웍 자원은 접근할 수 없다.! 
 */
public class ShutdownHook {
    
    public ShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                //전역 자원을 해제하는 코드 삽입.
                System.out.println("start");
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    ExceptionUtil.castToRuntimeException(e);
                }
                System.out.println("end");
            }
        });
    }
    
    public static void main(String[] arg){
        new ShutdownHook();
        System.out.println("zzzz");
    }

    /** JUnit실행시 자동으로 하나 생성된다. */
    @Test
    public void qwe(){
        System.out.println("zzzz");
        
    }

    
}
