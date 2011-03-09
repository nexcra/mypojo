package erwins.util.lib.security;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 이 클래스는 SHA1(Secure Hash Algorithm)를 이용한 160비트 해시를 제공한다.
 * <a href="http://www.ietf.org/rfc/rfc3174.txt?number=3174">RFC-3174</a>로 지정되어 있으며 프로그램과 파일의 무결성 검사에 사용한다.
 * 미국 국가 안전 보장국(NSA)이 1993년에 처음으로 셜계했으며 미국 국가 표준으로 지정되었다.
 * SHA-1은 SHA 함수들 중 가장 많이 쓰이며, TLS, SSL, PGP, SSH, IPSec 등 많은곳에서 사용되고 있다.
 * 현재 공격법이 존재함으로 중요한 곳에는 SHA-256 이상의 알고리즘은 권장한다.
 */
public abstract class SHA1{
    
    private static final String ALGORITHM = "SHA1";

    
    /**
     * <p>입력한 데이터(바이트 배열)을 SHA1 알고리즘으로 처리하여 해쉬값을 도출한다.</p>
     * 
     * <pre>
     * SHA1Utils.getHash([0x68, 0x61, 0x6e]) = [0x4f, 0xf6, 0x15, 0x25, 0x34, 0x69, 0x98, 0x99, 0x32, 0x53, 0x2e, 0x92, 0x60, 0x06, 0xae, 0x5c, 0x99, 0x5e, 0x5d, 0xd6]
     * </pre>
     * 
     * @param input 입력 데이터(<code>null</code>이면 안된다.)
     * @return 해쉬값
     */
    public static byte[] getHash(byte[] input) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            // 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
            throw new IOException(ALGORITHM + " Algorithm Not Found", e);
        }
    }
    
    /**
     * <p>입력 스트림으로부터 데이터를 읽어와서, SHA1 알고리즘으로 처리하여 해쉬값을 도출한다.</p>
     * 
     * @param input 입력 스트림(<code>null</code>이면 안된다.)
     * @return 해쉬값
     * @throws IOException 입/출력 에러발생시
     */
    public static byte[] getHash(InputStream input) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = input.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
            throw new IOException(ALGORITHM + " Algorithm Not Found", e);
        }
    }
    
    /**
     * <p>파일로부터 데이터를 읽어와서, SHA1 알고리즘으로 처리하여 해쉬값을 도출한다.</p>
     * 
     * @param file 파일(<code>null</code>이면 안된다.)
     * @return 16진수 문자열로 변환한 해쉬값
     * @throws IOException 입/출력 에러발생시
     */
    public static byte[] getHash(File file) throws IOException {
        byte[] hash = null;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            hash = getHash(bis);
        } finally {
            if (bis != null) try { bis.close(); } catch(IOException ie) {}
        }
        return hash;
    }

    /**
     * <p>입력한 데이터(byte[])를 SHA1 알고리즘으로 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
     * 
     * <pre>
     * SHA1Utils.getHashHexString([0x68, 0x61, 0x6e]) = "4ff615253469989932532e926006ae5c995e5dd6"
     * </pre>
     * 
     * @param input 변환할 바이트 배열(<code>null</code>이면 안된다.)
     * @return 16진수 문자열로 변환한 
     */
    public static String getHashHexString(byte[] input) throws IOException {
        byte[] hash = getHash(input);
        StringBuffer sb = new StringBuffer(); 
        for (int i = 0; i < hash.length; i++) { 
             sb.append(Integer.toString((hash[i] & 0xf0) >> 4, 16)); 
             sb.append(Integer.toString(hash[i] & 0x0f, 16));
        } 
        return sb.toString();
    }
    
    /**
     * <p>입력한 데이터(String)를 SHA1 알고리즘으로 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
     * 
     * <pre>
     * SHA1Utils.getHashHexString("")    = "da39a3ee5e6b4b0d3255bfef95601890afd80709"
     * SHA1Utils.getHashHexString("a")   = "86f7e437faa5a7fce15d1ddcb9eaeaea377667b8"
     * SHA1Utils.getHashHexString("han") = "4ff615253469989932532e926006ae5c995e5dd6"
     * </pre>
     * 
     * @param input 입력 데이터(<code>null</code>이면 안된다.)
     * @return 16진수 문자열로 변환한 
     */
    public static String getHashHexString(String input) throws IOException {
        return getHashHexString(input.getBytes());
    }
    
    /**
     * <p>입력한 데이터(String)를 SHA1 알고리즘으로 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
     * 
     * <pre>
     * SHA1Utils.getHashHexString("", "ISO-8859-1")    = "da39a3ee5e6b4b0d3255bfef95601890afd80709"
     * SHA1Utils.getHashHexString("a", "ISO-8859-1")   = "86f7e437faa5a7fce15d1ddcb9eaeaea377667b8"
     * SHA1Utils.getHashHexString("han", "ISO-8859-1") = "4ff615253469989932532e926006ae5c995e5dd6"
     * </pre>
     * 
     * @param input 입력 데이터(<code>null</code>이면 안된다.)
     * @param charsetName the name of a supported {@link java.nio.charset.Charset}
     * @return
     */
    public static String getHashHexString(String input, String charsetName) throws IOException {
        return getHashHexString(input.getBytes(charsetName));
    }
    
    /**
     * <p>입력 스트림으로부터 데이터를 읽어와서, SHA1 알고리즘으로 처리하여 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
     * 
     * @param input 입력 스트림(<code>null</code>이면 안된다.)
     * @return 16진수 문자열로 변환한 해쉬값
     * @throws IOException 입/출력 에러발생시
     */
    public static String getHashHexString(InputStream input) throws IOException {
        byte[] hash = getHash(input);
        StringBuffer sb = new StringBuffer(hash.length * 2); 
        for (int i = 0; i < hash.length; i++) { 
             sb.append(Integer.toString((hash[i] & 0xf0) >> 4, 16)); 
             sb.append(Integer.toString(hash[i] & 0x0f, 16));
        } 
        return sb.toString();
    }
    
    
    /**
     * <p>파일로부터 데이터를 읽어와서, SHA1 알고리즘으로 처리하여 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
     * 
     * @param file 파일(<code>null</code>이면 안된다.)
     * @return 16진수 문자열로 변환한 해쉬값
     * @throws IOException 입/출력 에러발생시
     */
    public static String getHashHexString(File file) throws IOException {
        byte[] hash = getHash(file);
        StringBuffer sb = new StringBuffer(hash.length * 2);
        for (int i = 0; i < hash.length; i++) { 
             sb.append(Integer.toString((hash[i] & 0xf0) >> 4, 16)); 
             sb.append(Integer.toString(hash[i] & 0x0f, 16));
        }
        return sb.toString();
    }

}
