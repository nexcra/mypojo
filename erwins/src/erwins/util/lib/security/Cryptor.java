
package erwins.util.lib.security;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;

import com.oreilly.servlet.Base64Decoder;
import com.oreilly.servlet.Base64Encoder;

import erwins.util.counter.Latch;
import erwins.util.exception.MalformedException;
import erwins.util.lib.Strings;

/**
 * 암호화 , 복호화, Base64, 해쉬화 , 해쉬값 검증 등의 작업 Base64란 2진 데이터를, 문자코드에 영향을 받지 않는 공통
 * ASCII 영역의 문자들로만 이루어진 일련의 문자열로 바꾸는 방식을 가리키는 개념으로서,간단히 64진수로 보면 됩니다. 오라일리의 jar가
 * 필요하다..
 * 
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class Cryptor {

    private static final String DEFAULT_KEY = "quantum.object@hotmail.com";

    /**
     * Base64로 인코딩한다.
     */
    public static String base64Encode(String str){
        return Base64Encoder.encode(str.getBytes());
    }

    /**
     * Base64를 디코딩한다.
     */
    public static String base64Decode(String str){
        return Base64Decoder.decode(str);
    }

    /**
     * TripletDes방식으로 암호화 한다.
     */
    public static String encrypt(String baseStr,String ... key){
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding"); //String으로 모드 설정
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(key));
            byte[] plainText = baseStr.getBytes("UTF8");

            // 암호화 시작
            byte[] cipherText = cipher.doFinal(plainText);

            return Strings.getStr(cipherText);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 암호화한 String을 char int 형으로 분리한다. 테스트 위해 제작
     */
    public static String encryptInt(String baseStr,String ... key){
        StringBuilder b = new StringBuilder();
        Latch l = new Latch();
        for (char ch : encrypt(baseStr,key).toCharArray()) {
            if (!l.next()) b.append(",");
            b.append((int) ch);
        }
        return b.toString();
    }

    /**
     * char int 형으로 분리된 문자열을 복호화 한다. 테스트 위해 제작
     */
    public static String decryptInt(String baseStr,String ... key){
        StringBuilder b = new StringBuilder();
        for (String ch : baseStr.split(",")) {
            b.append((char) Integer.parseInt(ch));
        }
        baseStr = b.toString();
        return decrypt(baseStr,key);
    }

    /**
     * TripletDes방식으로 복호화 한다.
     */
    public static String decrypt(String baseStr,String ... key){

        try {
            //복호화 모드로서 초기화
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding"); //String으로 모드 설정
            cipher.init(Cipher.DECRYPT_MODE, makeKey(key));
            //복호화 수행
            byte[] decryptedText = cipher.doFinal(Strings.getByte(baseStr));
            String output = new String(decryptedText, "UTF8");
            return output;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * DESede를 사용할 때는 key 길이는 24여야 합니다? key 길이가 16byte 라면 DESede 대신 DES 를 사용하시면
     * 됩니다.?
     **/
    private static SecretKey makeKey(String[] key){

        byte[] keydata = null;
        if (key.length == 0) keydata = DEFAULT_KEY.getBytes();
        else keydata = key[0].getBytes();

        SecretKey desKey;
        try {
            DESedeKeySpec keySpec = new DESedeKeySpec(keydata);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            desKey = keyFactory.generateSecret(keySpec);
        }
        catch (Exception e) {
            throw new MalformedException("적절한 key가 아닙니다. DESede를 사용할 때는 key 길이는 24여야 합니다");
        }
        return desKey;
    }

    /**
     * MD5를 이용하여 String객체를 해쉬화 한다. VChar 32
     */
    public static String hash(String str){
        try {
            return MD5.getHashHexString(str);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}