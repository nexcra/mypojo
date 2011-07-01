
package erwins.util.lib.security;

import java.util.Calendar;

import erwins.util.lib.DayUtil;

/**
 * 암호화 , 복호화, Base64, 해쉬화 , 해쉬값 검증 등의 작업 Base64란 2진 데이터를, 문자코드에 영향을 받지 않는 공통
 * ASCII 영역의 문자들로만 이루어진 일련의 문자열로 바꾸는 방식을 가리키는 개념으로서,간단히 64진수로 보면 됩니다. 오라일리의 jar가
 * 필요하다..
 */
public abstract class MD5s extends MD5{

	/** 단순 래핑 */
	public static String hash(String input){
        return getHashHexString(input);
    }

    /**
     * 1. 오늘 년월일 기준으로 key 생성
     */
    public static String hashByDate(String baseStr) {
        return getHashHexString(DayUtil.DATE_SIMPLE.get() + baseStr);
    }
    
    /**
     * 2. hashByDate로 생성된 해쉬값과 String을 비교하여 일치여부를 판단한다.
     * @param day : 매치 가능한 일자. ex) 3 => 3일 이내로 생성된 hash값 true
     */
    public static boolean isMatchByDay(String hashed,String key,int day) {
        Calendar sysdate = Calendar.getInstance();
        for (int i = 0; i < day; i++) {
            sysdate.add(Calendar.DATE, -i);
            if (isMatch(hashed, DayUtil.DATE_SIMPLE.get(sysdate) + key)) return true;
        }
        return false;
    }

    /**
     * 해쉬값과 String을 비교하여 일치여부를 판단한다.
     */
    public static boolean isMatch(String hashed,String key) {
        return hashed.equals(getHashHexString(key));
    }

}