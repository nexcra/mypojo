package erwins.util.nio;

/** 뭔가 허접하다.. 쓸모없는듯 ㅠㅠ */
public abstract class ThreadUtil{
    
    /** 간이 테스트용 */
    public static void sleep(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            //무시한다.
        }
    }

}
