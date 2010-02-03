
package erwins.util.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 캐릭터 변환에 관한 모음이다.
 */
public class CharSets {
    
    public static final String EUC_KR = "EUC-KR";
    public static final String UTF_8 = "UTF-8";
    public static final String UNICODE = "UNICODE";

    /**
     * UTF-8로 인코딩을 바꾼다.
     */
    public static String getUtf8(String str){
        try {
            return new String(str.getBytes("8859_1"), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * UTF-8로 인코딩을 바꾼다.
     */
    public static String getUtf8FromEucKr(String str){
        try {
            return new String(str.getBytes(EUC_KR), UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 웹 기본인코딩(IE)을 EUC-KR로 인코딩을 바꾼다.
     */
    public static String getEucKr(String str){
        try {
            return new String(str.getBytes("8859_1"), "EUC-KR");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * String을 URL로 인코딩 한다. 플랫폼에 유영하지 않음!!
     * 이거 대채할것 찾기
     */
    public static String toUrl(String s){
        if (s == null) return null;
        String result = null;
        try {
            result = URLEncoder.encode(s,"UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException("PinUtil.URLEncode(\""+s+"\")\r\n"+ex.getMessage());
        }
        return result;
    }

}
