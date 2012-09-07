
package erwins.util.lib;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;

import erwins.util.valueObject.Won;




/**
 *  각 객체의 널 제거는 reflection이나 ORM에서 제공해주는것을 사용하기 바란다. 
 *  <br> To : 프레젠테이션 용의 String문자열로의 변환 Get : 스크립팅 또는 데이터 포맷 변환용
 */
public enum FormatUtil {
    
    /**
     * 퍼센트??
     */
    @Deprecated
    PERCENT(NumberFormat.getPercentInstance()),

    /**
     * #,##0
     */
    INT(new DecimalFormat("#,##0")),
    
    /**
     * #,##0.0
     */
    DOUBLE1(new DecimalFormat("#,##0.0")),

    /**
     * #,##0.00
     */
    DOUBLE2(new DecimalFormat("#,##0.00"));


    private Format format;

    private FormatUtil(Format format) {
        this.format = format;
    }

    /**
     * 패턴에 따라 String을 리턴해줌
     * @return ex) toFormat("123456",*.FORMAT_INT) => "123,456"
     */
    public String get(String str) {
        return format.format(StringUtil.getDoubleValue(str));
    }
    
    public String get(Number value) {
        return format.format(value);
    }

    /**
     * 게시판의 제목 등등이 길때 ~~... 으로 줄여준다.
     * HTML은 과감히 잘라준다... 줄인 후에 잘라야 할거 같기도 하고.. 에고
     * @param1 원본 str
     * @param2 최대길이
     */
    public static String toShorten(String str, int maxCount) {
        //str = RegEx.stripHTML(str);
        return toShorten(str, maxCount, "...");
    }
    
    /**
     * 숫자를 String형태의 문자열로 바꿔준다.
     * ex) 10000 => 1만원 
     */
    public static String toWon(BigDecimal money) {
        return new Won(money).toString();
    }

    /**
     * 게시판의 제목 등등이 길때 ~~... 으로 줄여준다.
     * 
     * @param1 원본 str
     * @param2 최대길이
     */
    public static String toShorten(String str, int maxCount, String shorter) {
        return (str.length() > maxCount) ? str.substring(0, maxCount) + shorter : str;
    }
    
    /** 소문자로 바꿔준다~ 체크는 안함 귀찮.. 
     * 97 ~ 122 : a~z --> 0부터 시작
		65 ~ 90   : A ~ Z*/
    public static String intToAlpha(int i) {
    	return new String(new char[]{(char)(i+97)});
    }

    /**
     * 10자리 사업자등록번호를 '-'를 삽입하여 반환한다.
     */
    public static String toBid(String str) {
        if (str.length() != 10) return str;
        String message = "{0}-{1}-{2}";
        Object[] args = { str.substring(0, 3), str.substring(3, 5), str.substring(5, 10) };
        return MessageFormat.format(message, args);
    }

    /**
     * reflection된 10자리 사업자등록번호를 '-'를 삽입하여 반환한다.
     */
    public static String toBid(Object obj, String name){
        String money = (String) ReflectionUtil.findFieldValue(obj, name);;
        return toBid(StringUtil.nvl(money));
    }

    /**
     * 6-7자리 주민등록번호 '-'를 삽입하여 반환한다.
     */
    public static String toSid(String str) {
        if (str.length() != 13) return str;
        String message = "{0}-{1}";
        Object[] args = { str.substring(0, 6), str.substring(6, 13) };
        return MessageFormat.format(message, args);
    }

    /**
     * 10~11자리 전화번호를 '-'를 삽입하여 반환한다.
     */
    public static String toTel(String str) {
        if (str == null) return "";
        int len = str.length();
        if (len < 9) return str;

        String message = "{0}-{1}-{2}";
        Object[] args = new String[3];
        String body = null;

        if (str.startsWith("01")) { //무선
            args[0] = str.substring(0, 3);
            body = str.substring(3);
        } else if (str.startsWith("02")) { //서울
            args[0] = str.substring(0, 2);
            body = str.substring(2);
        } else { //지방
            args[0] = str.substring(0, 3);
            body = str.substring(3);
        }

        if (body.length() == 7) {
            args[1] = body.substring(0, 3);
            args[2] = body.substring(3);
        } else if (body.length() == 8) {
            args[1] = body.substring(0, 4);
            args[2] = body.substring(4);
        } else {
            message = "{0}-{1}";
            args[1] = body;
        }

        return MessageFormat.format(message, args);
    }
    
    /**
     * 날자 형식의 String에 구분자를 넣어준다. (yyyy-MM-dd) Carendar를 사용하지 않기 위한 편법.
     * @param 8자리 yyyyMMdd형식의 String
     */
    public static String toDate(String yyyyMMdd,String seperator) {
        if (StringUtil.isEmpty(yyyyMMdd)) return "";
        if(yyyyMMdd.length()==8) return yyyyMMdd.substring(0,4)+seperator + yyyyMMdd.substring(4,6)+seperator + yyyyMMdd.substring(6,8);
        else if(yyyyMMdd.length()==6) return yyyyMMdd.substring(0,4)+seperator + yyyyMMdd.substring(4,6);
        else return yyyyMMdd;
    }
    
    /**
     * yyyyMMdd사이에 -를 추가해서 돌려준다.
     */
    public static String toDate(String yyyyMMdd) {
        return toDate(yyyyMMdd,"-");
    }    
    
    // ===========================================================================================
    //                            getter / setter        
    // ===========================================================================================

    /**
     * @return
     * @uml.property  name="format"
     */
    public Format getFormat() {
        return format;
    }


}