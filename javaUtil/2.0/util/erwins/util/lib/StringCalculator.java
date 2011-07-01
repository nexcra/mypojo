
package erwins.util.lib;

import java.math.BigDecimal;

/**
 * 간단한 String을 조합해 계산이 가능하다.
 * 절대로 비지니스 로직에 사용하지 말것.
 */
public abstract class StringCalculator {
    
    public static String Plus(String A, String B) {
        // A + B
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());
        B_A = B_A.add(B_B);
        return B_A.toString();
    }
    

    public static String Plus(String A, String B, int Scale) {
        // A + B
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        B_A = B_A.add(B_B).setScale(Scale, 1);

        return B_A.toString();
    }

    public static String Minus(String A, String B) {
        // A - B
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        B_A = B_A.add(B_B.negate());

        return B_A.toString();
    }

    public static String Minus(String A, String B, int Scale) {
        // A - B
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        B_A = B_A.add(B_B.negate()).setScale(Scale, 1);

        return B_A.toString();
    }

    public static String Multiply(String A, String B) {
        // A * B
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        B_A = B_A.multiply(B_B);

        return B_A.toString();
    }

    public static String Multiply(String A, String B, int Scale) {
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        B_A = B_A.multiply(B_B).setScale(Scale, 1);

        return B_A.toString();
    }

    public static String Divide(String A, String B) {
        int Scale = 30;
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        B_A = B_A.divide(B_B, Scale, 1);

        return B_A.toString();
    }

    public static String Divide(String A, String B, int Scale) {
        // Scale이 3일 경우 소수 셋째자리까지보여준다.
        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        B_A = B_A.divide(B_B, Scale, 1);

        return B_A.toString();
    }

    /**
     *  -1, 0 or 1 as A is numerically less than, equal to, or greater than
     * A > B : Compare(A,B) > 0
     * A < B : Compare(A,B) < 0
     * A == B : Compare(A,B) == 0
     * A >= B : Compare(A,B) >= 0
     * A <= B : Compare(A,B) <= 0
     * A != B : Compare(A,B) != 0
     */
    public static int Compare(String A, String B) {

        BigDecimal B_A = new BigDecimal(A.trim());
        BigDecimal B_B = new BigDecimal(B.trim());

        return B_A.compareTo(B_B);
    }
    
    /**
     * 앞의것이 뒤의것보다 크면 true  
     */
    public boolean isLarge(String A, String B){
        if(Compare(A,B) > 0 ) return true;
        return false;
    }

    public static String Round(String A, int Scale) {
        BigDecimal B_A = new BigDecimal(A.trim());
        B_A = B_A.setScale(Scale, BigDecimal.ROUND_HALF_UP);
        return B_A.toString();
    }

    public static String Floor(String A, int Scale) {
        BigDecimal B_A = new BigDecimal(A.trim());
        B_A = B_A.setScale(Scale, BigDecimal.ROUND_DOWN);
        return B_A.toString();
    }

    public static String Updigt(String A, int Scale) {
        BigDecimal B_A = new BigDecimal(A.trim());
        B_A = B_A.setScale(Scale, BigDecimal.ROUND_UP);
        return B_A.toString();
    }

}
