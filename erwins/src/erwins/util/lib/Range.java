
package erwins.util.lib;

/**
 * 그루비의 Range를 흉내내자.
 */
public class Range {

    /** 10 입력시 1~10까지의 Integer배열을 리턴한다. */
    public static Integer[] to(int size) {
        Integer[] result = new Integer[size];
        for(int i=0;i<size;i++){
            result[i] = i;
        }
        return result;
    }

}