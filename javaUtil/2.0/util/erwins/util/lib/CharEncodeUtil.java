
package erwins.util.lib;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.lang.CharEncoding;

/**
 * 캐릭터 변환에 관한 모음이다.
 * static메소드 쓰지 말것! 나중에 필요하면 enum으로 만들자. TimeUtil 참고.
 */
public abstract class CharEncodeUtil extends CharEncoding{
	
	//====================  캐릭터셑 문자열 추가  ==============================
	public static final String EUC_KR = "EUC-KR";
	public static final String UNICODE = "UNICODE";
	
	//====================  캐릭터셑  추가  ==============================
	public static final Charset C_EUC_KR = Charset.forName(EUC_KR);
	public static final Charset C_UTF_8 = Charset.forName(UTF_8);

    /**
     * UTF-8로 인코딩을 바꾼다.
     */
    public static String getUtf8(String str){
        try {
            return new String(str.getBytes(ISO_8859_1), UTF_8);
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
