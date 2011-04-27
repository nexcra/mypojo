
package erwins.util.lib;

import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

/**
 * apache의 StringUtils에 없는것을 정의한다.
 */
public class RandomStringUtil extends RandomStringUtils {

    /**
     * 랜덤한 사업자 등록번호를 가져온다.
     */
    public static String makeRandomBid() {
        while (true) {
            String bid = RandomStringUtils.randomNumeric(10);
            if (StringUtil.isBusinessId(bid)) { return bid; }
        }
    }

    /**
     * 랜덤한 주민등록번호를 가져온다.
     * 연령을 20~60세로 제한한다.
     */
    public static String makeRandomSid() {
        int yy = DayUtil.YY.getIntValue();
        while (true) {
            String sid = RandomStringUtils.randomNumeric(13);
            int birth = Integer.parseInt(sid.substring(0,2));
            if (StringUtil.isSid(sid)) {
                int value = Integer.parseInt(String.valueOf(sid.charAt(6)));
                int age = yy - birth;
                switch(value){
                    case 1: case 2: age += 100; break;
                    case 3: case 4: break;
                    default : continue;
                }
                if(age > 20 && age < 60) return sid;
            }
        }
    }

    /**
     * n개의 랜덤 문자열을 가져온다. 개선의 여지가 있음. 이건 샘플용임
     * RandomStringUtils.randomAlphanumeric을 사용해도 됨.
     */
    public static String getRandomSring(int len) {
        String randomStr = "abcdefghijklmnopqrstuvwxyz123456789";
        StringBuffer strB = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            int rdIdx = random.nextInt(35);
            strB.append(randomStr.substring(rdIdx, rdIdx + 1));
        }
        return strB.toString();
    }

}
