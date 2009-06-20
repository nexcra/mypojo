
package erwins.util.valueObject;

import java.text.MessageFormat;

/**
 * 시분초를 나타내는 범용 TimeClass
 */
public class ShowTime {

    /**
     * @uml.property  name="totalNanoSecond"
     */
    long totalNanoSecond;
    long totalSecond;
    int h;
    int MM;
    int ss;

    public ShowTime(long nanoTime) {
        totalNanoSecond = nanoTime;
        init();
    }

    private void init() {
        totalSecond = totalNanoSecond / 1000 / 1000 / 1000;
        h = (int) (totalSecond / 60 / 60);
        MM = (int) (totalSecond / 60);
        ss = (int) (totalSecond % 60);
    }

    public boolean isLarge(int second) {
        return totalSecond > second ? true : false;
    }

    public boolean isLarge(long nanoSecond) {
        return totalNanoSecond > nanoSecond ? true : false;
    }

    public String toString() {
        return MessageFormat.format("{0}:{1}:{2}", h, MM, ss);
    }

    /** 예쁘게 보기~ */
    public String toString(int second) {
        if (isLarge(second)) return MessageFormat.format("{0}:{1}:{2}", h, MM, ss);
        else if (isLarge(1000000L)) return MessageFormat.format("{0}ms", totalNanoSecond / 1000 / 1000); //milli        
        else return MessageFormat.format("{0}ns", totalNanoSecond);
    }

    /**
     * @return
     * @uml.property  name="totalNanoSecond"
     */
    public long getTotalNanoSecond() {
        return totalNanoSecond;
    }

}