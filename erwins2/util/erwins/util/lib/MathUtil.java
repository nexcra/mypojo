
package erwins.util.lib;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 이 클래스는 수학 관련 함수를 제공합니다. inner static class 패턴 by Effective Java
 */
public enum MathUtil {

    /**
     * @uml.property  name="pLUS"
     * @uml.associationEnd  
     */
    PLUS(new Operation("+") {
        @Override
        public BigDecimal eval(BigDecimal body,BigDecimal ... values) {
            for(BigDecimal value : values) body = body.add(value);
            return body;
        }
    }),
    /**
     * @uml.property  name="mINUS"
     * @uml.associationEnd  
     */
    MINUS(new Operation("-") {
        @Override
        public BigDecimal eval(BigDecimal body,BigDecimal ... values) {
            for(BigDecimal value : values) body = body.add(value.negate());
            return body;
        }
    }),
    /**
     * @uml.property  name="mULTIPLY"
     * @uml.associationEnd  
     */
    MULTIPLY(new Operation("*") {
        @Override
        public BigDecimal eval(BigDecimal body,BigDecimal ... values) {
            for(BigDecimal value : values) body = body.multiply(value);
            return body;
        }
    }),
    /**
     * @uml.property  name="dIVIDE"
     * @uml.associationEnd  
     */
    DIVIDE(new Operation("/") {
        @Override
        public BigDecimal eval(BigDecimal body,BigDecimal ... values) {
            for(BigDecimal value : values) body = body.divide(value,BigDecimal.ROUND_HALF_UP);
            return body;            
        }
    });
    
    /** 100 */
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /**
     * @uml.property  name="op"
     * @uml.associationEnd  
     */
    private Operation op;

    private MathUtil(Operation op) {
        this.op = op;
    }
    public BigDecimal run(BigDecimal body,BigDecimal... values) {
        if(values.length < 1) throw new RuntimeException(values.length + " 1개 이상의 인자를 입력하셔야 합니다.");
        BigDecimal result = op.eval(body,values); 
        return result;
    }
    /** 반올림합니다. */
    public BigDecimal run(Integer scale,BigDecimal body,BigDecimal... values) {
        BigDecimal result = run(body,values); 
        return round(result,scale);
    }

    /**
     * 함수 원형을 설정한다.
     */
    public static abstract class Operation {
        private final String name;
        Operation(String name){
            this.name = name;
        }
        @Override
        public String toString(){
            return this.name;
        }
        public abstract BigDecimal eval(BigDecimal body,BigDecimal ... values);
    }

    /**
     * 내림 연산한다. <br> i가 0보다 작으면 스케일을 0으로 조정해야 ORM이 숫자(.toString())로 인식한다.
     */
    public static BigDecimal floor(BigDecimal value, int i) {
        BigDecimal adjusted = value.setScale(i, BigDecimal.ROUND_FLOOR);
        if (i <= 0) adjusted = adjusted.setScale(0);
        return adjusted;
    }

    /**
     * 반올림 연산한다. <br> i가 0보다 작으면 스케일을 0으로 조정해야 ORM이 숫자(.toString())로 인식한다.
     */
    public static BigDecimal round(BigDecimal value, int i) {
        BigDecimal adjusted = value.setScale(i, BigDecimal.ROUND_HALF_UP);
        if (i <= 0) adjusted = adjusted.setScale(0);
        return adjusted;
    }
    /**
     * double의 반올림 연산한다.
     */
    public static double round(double value, int i) {
        double buff = Math.pow(10,i);
        long result = Math.round(value * buff);
        return result / buff;
    }

    /**
     * 두 값의 차이의 절대값을 반환한다. 
     * 하나라도 null이면 null을 리턴한다.
     */
    public static BigDecimal interval(BigDecimal a,BigDecimal b) {
        if(a==null || b==null) return null; 
        return  a.add(b.negate()).abs();
    }

    /**
     * value가 Zero보다 큰지? zero이면 false를 리턴한다.
     * null이면 nullAction을 리턴한다.
     */
    public static Boolean isPositive(BigDecimal value,Boolean nullAction) {
        if (value == null) return nullAction;
        if (BigDecimal.ZERO.compareTo(value) < 0) return true;
        return false;
    }
    
    /**
     * value가 Zero보다 큰지?
     * null이거나 zero이면 false를 리턴한다.
     */
    public static Boolean isPositive(BigDecimal value) {
        if (value == null) return false;
        if (BigDecimal.ZERO.compareTo(value) < 0) return true;
        return false;
    }

    /**
     * value가 Zero보다 작은지?
     * null이거나 zero이면 false를 리턴한다.
     */
    public static boolean isNegative(BigDecimal value) {
        if (value == null) return false;
        if (BigDecimal.ZERO.compareTo(value) > 0) return true;
        return false;
    }
    
    /**
     * 크기를 비교한다.  
     * 같거나 null이면 false를 리턴한다.
     */
    public static boolean isLarge(BigDecimal A,BigDecimal B) {
        return isLarge(A,B,false);
    }
    
    /**
     * 숫자형으로 바꾼 뒤 크기를 비교한다. 
     * 입력값이 null이거나 같으면 false를 리턴한다.
     */
    public static Boolean isLarge(String A,String B) {
        if (A == null || B==null) return false;
        return isLarge(new BigDecimal(A),new BigDecimal(B),false);
    }
    
    /**
     * 크기를 비교한다. 비교 불가(null)이거나 같으면 same 리턴한다.
     */    
    public static Boolean isLarge(BigDecimal A,BigDecimal B,Boolean same) {
        if (A == null || B==null) return same;
        int compare = A.compareTo(B);
        if(compare==0) return same;
        else if (compare > 0) return true;
        else return false;
    }

    /**
     * A-B를 구한다. 결과가 음수라면 0을 리턴한다.
     */
    public static BigDecimal getInterval(BigDecimal A, BigDecimal B) {
        BigDecimal interval = A.add(B.negate());
        if (isNegative(interval)) return BigDecimal.ZERO;
        return interval;
    }
    
    /**
     * A/sum의 %를 구한다.
     * p는 보통 8 정도?
     */
    public static BigDecimal getRate(BigDecimal sum, BigDecimal value,int p) {
        if(isZero(sum,value)) return BigDecimal.ZERO;
        return value.divide(sum,p,BigDecimal.ROUND_HALF_UP).multiply(HUNDRED);
    }
    
    /**
     * Number를 Long으로 변환해서 계산한다. 편의용 메소드. 
     */
    public static BigDecimal getRate(Number sum, Number value,int p) {
        return getRate(new BigDecimal(sum.longValue()),new BigDecimal(value.longValue()),p);
    }
    
    /**
     * null 또는 0인지 확인한다.  나누기 등의 연산시 체크용으로 사용한다. 
     * 없거나 하나라도 null이면 true를 리턴한다.
     * 하나로도 zero이면 true를 리턴한다.
     */
    public static boolean isZero(BigDecimal ... values){
        if(CollectionUtil.isNullAny(values)) return true;
        if(CollectionUtil.isEqualsAny(BigDecimal.ZERO,values)) return true;
        return false;
    }
    
    public static <T extends Number> List<T> wrap(List<Number> org,Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        for(Number each : org){
            Number value = null;
            if(clazz == Integer.class) value = each.intValue();
            else if(clazz == Long.class) value = each.longValue();
            else value = new BigDecimal(each.longValue());;
            list.add(clazz.cast(value));
        }
        return list;
    }
    
    /** null과 Number가 아닌덧을 무시하고 더하는 로직. 소수점 이하는 더할 수 없다. */
    public static long sum(Object ... datas) {
    	long sum = 0;
    	for(Object each : datas){
    		if(each==null) continue;
    		if(!(each instanceof Number)) continue; 
    		sum+= ((Number)each).longValue();
    	}
    	return sum;
    }

    /**
     * <p>빠른 m^e mod n 연산을 수행한다.</p>
     * 
     * @param m
     * @param e
     * @param n
     * @return m^e mod n의 연산 결과
     */
    public static long fastExp(long m, long e, long n) {
        long z = 1;
        // 지수의 비트수와 일치할때까지 반복한다.
        while (e != 0) {
            // 지수의 특징 비트가 1일때
            while (e % 2 == 0) {
                // 이렇게 함으로써 지수의 비트수가 한 자리 줄어든다
                e = e / 2;
                m = (m * m) % n;
            }
            e--; // 지수의 특징 비트가 0이 되도록 한다.
            z = (z * m) % n; // 지수가 0일때 계산
        }
        return z;
    }

    /**
     * <p>확장된 유클리드 알고리즘(Extend Euclid Function)</p> bx mod p = 1 (bx + kp = 1)
     * 일때 x를 구한다.
     * 
     * @param b
     *            in GF(p)
     * @param p
     *            소수
     * @return x
     */
    public static long exEuclid(long b, long p) {
        long c = p;
        long d = b;
        long x = 0;
        long y = 1;
        while (d != 1) {
            long q = c / d;
            long e = c - d * q;
            long w = x - y * q;
            c = d;
            d = e;
            x = y;
            y = w;
        }
        if (y < 0) {
            y += p;
        }
        return y;
    }

    /**
     * <p>m, n의 최대공약수(最大公約數, greatest common measure)를 구한다.</p> <pre> 확장 유클리드
     * 알고리즘(Euclid's Algorithm) 사용 : gcd(m,n) = gcd(m%n, n) (단, m > n) </pre>
     * 
     * @param m
     * @param n
     * @return 최대공약수
     */
    public static long gcd(long m, long n) {
        long k = -1;

        if (n > m) {
            long t = m;
            m = n;
            n = t;
        }

        if (n == 0) {
            k = m;
        } else {
            k = gcd(m % n, n);
        }

        return k;
    }

    /**
     * <p>m, n, o의 최대공약수(最大公約數, greatest common measure)를 구한다.</p>
     * 
     * @param m
     * @param n
     * @param o
     * @return 최대공약수
     */
    public static long gcd(long m, long n, long o) {
        return gcd(gcd(m, n), o);
    }

    /**
     * <p>최대공약수(最大公約數, greatest common measure)를 구한다.</p>
     * 
     * @param mn
     * @return 최대공약수
     */
    public static long gcd(long[] mn) {
        long k = 0;
        int size = mn.length;
        if (size > 2) {
            long[] kn = new long[size / 2 + ((size % 2 == 0) ? 0 : 1)];
            int index = 0;
            for (int i = 0; i < mn.length; i += 2) {
                if (i + 1 < mn.length) {
                    kn[index++] = gcd(mn[i], mn[i + 1]);
                } else {
                    kn[index++] = mn[i];
                }
            }
            k = gcd(kn);
        } else {
            k = gcd(mn[0], mn[1]);
        }
        return k;
    }

    /**
     * <p>m, n의 최소공배수(最小公倍數, least common multiple)를 구한다.</p>
     * 
     * @param m
     * @param n
     * @return 최소공배수
     */
    public static long lcm(long m, long n) {
        long k = -1;
        long g = gcd(m, n);
        long a = m / g;
        long b = n / g;
        k = a * b * g;
        return k;
    }

    /**
     * <p>m, n, o의 최소공배수(最小公倍數, least common multiple)를 구한다.</p>
     * 
     * @param m
     * @param n
     * @param o
     * @return 최소공배수
     */
    public static long lcm(long m, long n, long o) {
        return lcm(lcm(m, n), o);
    }

    /**
     * <p>최소공배수(最小公倍數, least common multiple)를 구한다.</p>
     * 
     * @param mn
     * @return 최소공배수
     */
    public static long lcm(long[] mn) {
        long k = 0;
        int size = mn.length;
        if (size > 2) {
            long[] kn = new long[size / 2 + ((size % 2 == 0) ? 0 : 1)];
            int index = 0;
            for (int i = 0; i < mn.length; i += 2) {
                if (i + 1 < mn.length) {
                    kn[index++] = lcm(mn[i], mn[i + 1]);
                } else {
                    kn[index++] = mn[i];
                }
            }
            k = lcm(kn);
        } else {
            k = lcm(mn[0], mn[1]);
        }

        return k;
    }

    /**
     * <p>두 수가 서로소인지 아닌지를 판단한다.</p>
     * 
     * @param m
     * @param n
     * @return 서로소이면 true, 아니면 false
     */
    public static boolean isRelativelyPrime(long m, long n) {
        return gcd(m, n) == 1;
    }

    /**
     * <p>입력한 숫자가 소수인지 아닌지 판단한다.</p>
     * 
     * @param number
     *            입력한 숫자(1보다 커야한다)
     * @return 소수이면 true, 소수가 아니면 false를 반환
     * @throws IllegalArgumentException
     *             입력한 숫자가 2보다 작을때 발생
     */
    public static boolean isPrimeNumber(long number) throws IllegalArgumentException {
        if (number < 2) throw new IllegalArgumentException("1보다 큰 정수를 입력하세요.");
        boolean result = false;
        long count = 0;
        for (count = 2; number % count != 0; count++);
        if (count == number) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    /**
     * <p>입력한 숫자보다 큰 소수를 반환한다.</p>
     * 
     * @param number
     * @return
     * @throws IllegalArgumentException
     */
    public static long getPrimeNumber(long number) throws IllegalArgumentException {
        if (number < 2) throw new IllegalArgumentException("1보다 큰 정수를 입력하세요.");
        long primeNumber = number;
        long count = 0;
        for (long i = number; true; i++) {
            for (count = 2; i % count != 0; count++);
            if (count == i) {
                primeNumber = i;
                break;
            }
        }

        return primeNumber;
    }

    /**
     * <p>입력한 숫자보다 큰 소수를 반환한다. (밀러-라빈 소수 판정법 사용)</p>
     * 
     * @param number
     * @return
     * @throws IllegalArgumentException
     */
    public static long getFastPrimeNumber(long number) throws IllegalArgumentException {
        if (number < 2) throw new IllegalArgumentException("1보다 큰 정수를 입력하세요.");
        long primeNumber = number;
        for (long i = number; true; i++) {
            if (millerRabin(i)) {
                primeNumber = i;
                break;
            }
        }
        return primeNumber;
    }

    /**
     * <p>밀러-라빈 소수 판정법으로 소수 여부를 판단한다.</p>
     * 
     * <pre> 어떤수 n이 소수인지를 판단하는 확률적 알고리즘 이 알고리즘을 통과한 수는 ( 1 - 1/(4^100) )의 확률로
     * 소수라고 할 수 있다 </pre>
     * 
     * @param n
     * @return
     */
    public static boolean millerRabin(long n) {
        boolean isPrime = true;
        long m = n - 1;
        long k = 0;

        // n-1 = (2^k)*m 을 만족하는 m, k를 구한다. 
        while (m % 2 == 0) {
            m = m / 2;
            k++;
        }

        for (int i = 0; i < 100; i++) {
            long a = (long) (Math.random() * (n - 2));
            if (a < 2) {
                a = 2;
            }

            // 공약수가 있다면 소수가 아니다.
            if (gcd(a, n) == 1) {
                // b = a^m mod n 연산을 수행한다.
                long b = fastExp(a, m, n);
                // 강한 유사소수인지 판단
                if (b != 1 && b != (n - 1)) {
                    for (int j = 0; j < k - 1; j++) {
                        b = (b * b) % n;
                        if (b == (b - 1)) {
                            break;
                        }
                    }
                    // 소수가 아닌 경우
                    if (b != (b - 1)) {
                        isPrime = false;
                        break;
                    }
                }
            } else {
                isPrime = false;
            }
        }
        return isPrime;
    }

    /**
     * <p>약수를 구한다.</p>
     * 
     * @param number
     * @return
     * @throws IllegalArgumentException
     */
    public static long getDivisor(long number) throws IllegalArgumentException {
        if (number < 2) throw new IllegalArgumentException("1보다 큰 정수를 입력하세요.");
        long count = 0;
        for (count = 2; number % count != 0; count++);
        return count;
    }

    /**
     * <p>오일러 파이 함수(Euler Phi Function)</p>
     * 
     * @param n
     *            0보다 큰 정수
     * @return
     */
    public static long eulerPhi(long n) {
        if (n < 1) throw new IllegalArgumentException("0보다 큰 정수를 입력하세요.");
        long r = 0;
        if (n == 1 || n == 2) {
            r = n;
        } else {
            long p = getDivisor(n);
            if (n == p) {
                r = (n - 1);
            } else {
                long q = n / p;
                if (isRelativelyPrime(p, q)) {
                    // 서로소일 경우
                    r = (p - 1) * (q - 1);
                } else {
                    // 서로소가 아닐경우.
                    for (long i = 1; i < n; i++) {
                        if (isRelativelyPrime(n, i)) {
                            r++;
                        }
                    }
                }
            }
        }
        return r;
    }

    /**
     * <p>오일러 파이 함수(Euler Phi Function)</p>
     * 
     * @param p
     * @param q
     * @return
     */
    public static long eulerPhi(long p, long q) {
        if (!isPrimeNumber(p)) { throw new IllegalArgumentException("p는 소수여야합니다."); }
        if (!isPrimeNumber(q)) { throw new IllegalArgumentException("q는 소수여야합니다."); }
        if (!isRelativelyPrime(p, q)) { throw new IllegalArgumentException("p, q는 서로소야합니다."); }
        return (p - 1) * (q - 1);
    }
}
