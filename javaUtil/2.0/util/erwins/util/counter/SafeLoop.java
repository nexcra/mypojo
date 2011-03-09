
package erwins.util.counter;



/**
 * 무한루프 방지용
 */
public class SafeLoop extends AccumulatorTemplit{
    
    public SafeLoop(int threshold, Runnable command) {
        super(threshold, command);
    }

    public SafeLoop(int threshold) {
        super(threshold);
    }

    /**
     * 항상 true를 리턴한다.
     * 임계치를 넘어가면 command나 예외를 던진다.
     */
    public boolean next(){
        now++;
        if(now > threshold){
            if(command==null) throw new RuntimeException(now + " is too many loop");
            now = 0;
            command.run();
            return false;
        }
        return true;
    }

	@Override
	public int count() {
		return now;
	}


}