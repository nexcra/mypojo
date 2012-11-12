package erwins.util.lib;


/**
 * 간이 계산기. 
 * double 이상의 정밀도는 기대하기 힘듬. 빅데시말은 사용하지 말것
 * @author sin
 */
public abstract class CalculateUtil {
    
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
