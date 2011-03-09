package erwins.util.lib;

/**
 * 이 클래스는 16진수 관련 함수를 제공합니다. 
 * 어디선가 불펌. 쓸일 없을듯.
 */
public class HexUtil {

    /**
     * <p>16진수 문자열을 바이트 배열로 변환한다.</p>
     * <p>문자열의 2자리가 하나의 byte로 바뀐다.</p>
     * 
     * <pre>
     * HexUtils.toBytes(null)     = null
     * HexUtils.toBytes("0E1F4E") = [0x0e, 0xf4, 0x4e]
     * HexUtils.toBytes("48414e") = [0x48, 0x41, 0x4e]
     * </pre>
     * 
     * @param digits 16진수 문자열
     * @return
     * @throws NumberFormatException
     */
    public static byte[] toBytes(String digits) throws IllegalArgumentException, NumberFormatException {
        if (digits == null) {
            return null;
        }
        int length = digits.length();
        if (length % 2 == 1) {
            throw new IllegalArgumentException("For input string: \"" + digits + "\"");
        }
        length = length / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int index = i * 2;
            bytes[i] = (byte)(Short.parseShort(digits.substring(index, index+2), 16));
        }
        return bytes;
    }
    
    /**
     * <p>16진수 문자열을 바이트 배열로 변환한다.</p>
     * <p>공백을 제거한 문자열의 2자리가 하나의 byte로 바뀐다.</p>
     * 
     * @param digits 16진수 문자열
     * @param ignoreBlank 공백제거 여부
     * @return
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    public static byte[] toBytes(String digits, boolean ignoreBlank) throws IllegalArgumentException, NumberFormatException {
        if (digits == null) {
            return null;
        }
        if (ignoreBlank) {
            // 공백제거
            digits = digits.replace(" ", "");
        }
        int length = digits.length();
        if (length % 2 == 1) {
            throw new IllegalArgumentException("For input string: \"" + digits + "\"");
        }
        length = length / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int index = i * 2;
            bytes[i] = (byte)(Short.parseShort(digits.substring(index, index+2), 16));
        }
        return bytes;
    }

    /**
     * <p>unsigned byte(바이트)를 16진수 문자열로 바꾼다.</p>
     * 
     * HexUtils.toString((byte)1)   = "01"
     * HexUtils.toString((byte)255) = "ff"
     * 
     * @param b unsigned byte
     * @return
     */
    public static String toString(byte b) {
        StringBuffer result = new StringBuffer(3);
        result.append(Integer.toString((b & 0xF0) >> 4, 16));
        result.append(Integer.toString(b & 0x0F, 16));
        return result.toString();
    }
    
    /**
     * <p>unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.</p>
     * 
     * <pre>
     * HexUtils.toString(null)                   = null
     * HexUtils.toString([(byte)1, (byte)255])   = "01ff"
     * </pre>
     * 
     * @param bytes unsigned byte's array
     * @return
     */
    public static String toString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xF0) >> 4, 16));
            result.append(Integer.toString(b & 0x0F, 16));
        }
        return result.toString();
    }
    
    /**
     * <p>16진수 문자열을 <code>int</code>형으로 변환한다.</p>
     * 
     * <pre>
     * HexUtils.toInteger(null) = null
     * HexUtils.toInteger("68616E67") = 1751215719
     * HexUtils.toInteger("65656E61") = 1701146209
     * </pre>
     * 
     * @param digits 16진수 문자열(4바이트)
     * @return
     */
    public static Integer toInteger(String digits) {
        if (digits == null) {
            return null;
        }
//      if (digits.length() != (2 * 4)) {
//          throw new IllegalArgumentException("For input string: \"" + digits + "\"");
//      }
//      return ByteUtils.toInt(toBytes(digits), 0);
        return Integer.parseInt(digits, 16);
    }
}
