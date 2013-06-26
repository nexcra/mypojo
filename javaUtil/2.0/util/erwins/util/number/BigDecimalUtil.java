
package erwins.util.number;

import java.math.BigDecimal;
import java.math.RoundingMode;




/**
 *  무거운 놈이니까, 간단 표현에만 사용하고, 대규모 벌크 계산에는 사용하지 말것 
 */
public abstract class BigDecimalUtil {
    
    /** 퍼센트를 구한다. */
    public static BigDecimal percent(long top,long bot,int newScale){
        if(bot==0) return BigDecimal.ZERO;
        BigDecimal t = new BigDecimal(top).multiply(new BigDecimal(100));
        BigDecimal b = new BigDecimal(bot);
        return t.divide(b, newScale, RoundingMode.HALF_UP);
    }


}