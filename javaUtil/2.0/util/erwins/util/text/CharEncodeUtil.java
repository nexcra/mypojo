
package erwins.util.text;

import java.io.UnsupportedEncodingException;
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
	/**  CSV등을 읽을때는 반드시 EUC_KR가 아닌 MS949로 읽어야 뷁 같은 한글이 깨지지 않는다.  */
	public static final String MS949 = "MS949";
	/**  바이트 읽을때 기본. RandomAccessFile 쓸때 사용된다. 사실 자세한 내용은 모름.  앞에 D는 규칙때문에 그냥 붙였다.   */
	public static final String D8859_1 = "8859_1";
	
	
	//====================  캐릭터셑  추가  ==============================
	public static final Charset C_EUC_KR = Charset.forName(EUC_KR);
	public static final Charset C_UTF_8 = Charset.forName(UTF_8);
	public static final Charset C_MS949 = Charset.forName(MS949);
	public static final Charset C_8859_1 = Charset.forName(D8859_1);
	
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

}
