package erwins.util.lib.security;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 이 클래스는 MD5(Message-Digest algorithm 5)를 이용한 128비트 해시를 제공한다.
 * <a href="http://www.ietf.org/rfc/rfc1321.txt?number=1321">RFC-1321</a>로 지정되어 있으며 프로그램과 파일의 무결성 검사에 사용한다.
 * 1996년에는 MD5의 설계상 결함이 발견되어, 암호학자들은 SHA-1 같은 다른 알고리즘을 사용할 것을 권장한다. 
 */
public abstract class MD5{
    
    private static final String ALGORITHM = "MD5";

    
    /**
     * <p>입력한 데이터(바이트 배열)을 MD5 알고리즘으로 처리하여 해쉬값을 도출한다.</p>
     * 
     * <pre>
     * MD5Utils.getHash([0x68, 0x61, 0x6e]) = [0x83, 0x83, 0x23, 0x91, 0x02, 0x7a, 0x1f, 0x2f, 0x4d, 0x46, 0xef, 0x88, 0x2f, 0xf3, 0xa4, 0x7d]
     * </pre>
     * 
     * @param input 입력 데이터(<code>null</code>이면 안된다.)
     * @return 해쉬값
     * @throws Exception 
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
     * <p>입력 스트림으로부터 데이터를 읽어와서, MD5 알고리즘으로 처리하여 해쉬값을 도출한다.</p>
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
     * <p>파일로부터 데이터를 읽어와서, MD5 알고리즘으로 처리하여 해쉬값을 도출한다.</p>
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
     * <p>입력한 데이터(byte[])를 MD5로 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
     * 
     * <pre>
     * MD5Utils.getHashHexString([0x68, 0x61, 0x6e]) = "83832391027a1f2f4d46ef882ff3a47d"
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
     * <p>입력한 데이터(String)를 MD5로 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
     * 
     * <pre>
     * MD5Utils.getHashHexString("")    = "d41d8cd98f00b204e9800998ecf8427e"
     * MD5Utils.getHashHexString("a")   = "0cc175b9c0f1b6a831c399e269772661"
     * MD5Utils.getHashHexString("han") = "83832391027a1f2f4d46ef882ff3a47d"
     * </pre>
     * 
     * @param input 입력 데이터(<code>null</code>이면 안된다.)
     * @return 16진수 문자열로 변환한 
     */
    public static String getHashHexString(String input) throws IOException {
        return getHashHexString(input.getBytes());
    }

    
    /**
     * <p>입력 스트림으로부터 데이터를 읽어와서, MD5 알고리즘으로 처리하여 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
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
     * <p>파일로부터 데이터를 읽어와서, MD5 알고리즘으로 처리하여 해쉬값을 도출한 다음, 16진수 문자열로 변환하여 출력한다.</p>
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
