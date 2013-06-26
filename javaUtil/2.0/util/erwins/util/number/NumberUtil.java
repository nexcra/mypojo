package erwins.util.number;

import org.apache.commons.lang.math.NumberUtils;


/**
 * 간이 숫자 유틸
 * 빅데시말은 사용하지 말것
 * @author sin
 */
public abstract class NumberUtil extends NumberUtils {
    
    public static boolean isEmpty(Number num) {
        if(num==null) return true;
        if(num.doubleValue() == 0) return true;
        return false;
    }
    
    public static long nullSafeDiv(Long small,Long large) {
        if(small==null) return 0;
        if(isEmpty(large)) return 0;
        return small / large;
    }
    
    /**
     * sql의 between과 공일하다. 같은 값이라면 true를 리턴
     */
    public static boolean isBetween(Number start,Number end,Number value) {
        if(value.doubleValue() < start.doubleValue()) return false;
        if(value.doubleValue() > end.doubleValue()) return false;
        return true;
    }
    


}
