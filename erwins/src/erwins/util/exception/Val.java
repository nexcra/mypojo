package erwins.util.exception;

import erwins.util.lib.Clazz;
import erwins.util.tools.SystemInfo;


/**
 * Validation의 줄임말
 * 입력값이 false이면 @see {@link MalformedException}을 던진다.
 */
public abstract class Val{
    
    private static final String TEST_MESSAGE = "Test 오류 발생!";

    /**
     * 사용자가 정확한 값을 입력했는가? 
     */
    public static void isTrue(boolean isMustTrue,String message) {
        if(isMustTrue) return;
        throw new MalformedException(message);
   }
    /**
     * 사용자가 정확한 값을 입력했는가?
     * 이건 test용으로만 사용
     */
    public static void isTrue(boolean isMustTrue) {
        isTrue(isMustTrue,TEST_MESSAGE);
    }
    
    /**
     * 두개가 같은지?
     * 이건 test용으로만 사용
     */
    public static void isEquals(Object a,Object b) {
        isTrue(a.equals(b),TEST_MESSAGE);
    }
    
    /**
     * 사용자가 정확한 값을 입력했는가? 
     */
    public static void isPositive(Number count,String message) {
        if(count!=null && count.intValue() > 0) return;
        throw new MalformedException(message);
    }
    
    /**
     * 사용자가 정확한 값을 입력했는가?
     * 이건 test용으로만 사용
     */
    public static void isPositive(Number count) {
        isPositive(count,TEST_MESSAGE);
    }
    
    /**
     * 빈값이 아님.  
     */
    public static void isNotEmpty(Object obj,String message) {
        if(Clazz.isEmpty(obj)) throw new MalformedException(message);
   }
    
    /**
     * 빈값이 아님.  
     */
    public static void isNotEmpty(Object obj) {
        isNotEmpty(obj,TEST_MESSAGE);
    }
    
    /**
     * 예외를 강제로 던진다.
     * 테스트용이다. 테섭에서만 작동.
     * 예외를 강제로 일으키고 실수로 지우지 않을 경우를 대비한다.
     */
    public static void throwEx() {
        if(!SystemInfo.isServer()) throw new RuntimeException("test용 예외 입니다.");
    }
    
}
