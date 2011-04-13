
package erwins.jsample;

import java.math.BigDecimal;

import org.junit.Test;


public class DecimalSample{
    
    /**
     * toString
     * toPlainString
     * toEngineeringString
     * 세가지의 차이점을 알자
     * @throws Exception
     */
    @Test
    public void toStr() throws Exception {
        
        BigDecimal a1 = new BigDecimal("45012123123121231233123322.12121321231131");
        a1 = a1.setScale(-2,3);
        BigDecimal a2 = new BigDecimal("45012322.12");
        System.out.println(a1);
        System.out.println(a1.toPlainString());
        System.out.println(a1.toEngineeringString());
        
    }
    

}
