package erwins.util.lib.security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Encoder;
import erwins.util.exception.MalformedException;
import erwins.util.lib.Strings;

/**
 * 대칭키 방식인 Triple DES (a.k.a DESede)이다.  root를 지정 후 싱글톤으로 사용하자.
 * @author erwins(my.pojo@gmail.com)
 */
public class Cryptor {

	/** 이 key를 사용하면 String 복호화의 경우 padding오류가 난다. 주의 */
	public static SecretKey generateKey(){
		SecretKey key = null;
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance("DESede");
			key = keygen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return key;
	}
	
	/** 문자열 단일 암호화는 이것으로 생성하자. ex)quantum.object@hotmail.com  */
	public static SecretKey generateKey(String key) {
		byte[] keydata = key.getBytes();
		SecretKey desKey;
		try {
			DESedeKeySpec keySpec = new DESedeKeySpec(keydata);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			desKey = keyFactory.generateSecret(keySpec);
		} catch (Exception e) {
			throw new MalformedException("DESede's key length must be 24!");
		}
		return desKey;
	}

	/** Save the specified TripleDES SecretKey to the specified file */
	public static void writeKey(SecretKey key, File f){
		try {
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
			DESedeKeySpec keyspec = (DESedeKeySpec) keyfactory.getKeySpec(key, DESedeKeySpec.class);
			byte[] rawkey = keyspec.getKey();

			// Write the raw key to the file
			FileOutputStream out = new FileOutputStream(f);
			out.write(rawkey);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Read a TripleDES secret key from the specified file */
	public static SecretKey readKey(File f){
		SecretKey key;
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			byte[] rawkey = new byte[(int) f.length()];
			in.readFully(rawkey);
			in.close();

			// Convert the raw bytes to a secret key like this
			DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
			key = keyfactory.generateSecret(keyspec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return key;
	}
	
	/** DESede 문자열 모드?? 사실 먼지 모르겠음. */
	private static final String DESede_STRING_MODE = "DESede/ECB/PKCS5Padding";
	
	public static String encryptText(SecretKey key, String str){
		return Strings.getStr(encrypt(key,str));
	}
	public static String encryptBase64(SecretKey key, String str){
		BASE64Encoder encoder = new BASE64Encoder();
		byte[] raw = encrypt(key,str);
        return encoder.encode(raw);
	}
	
	/** 바이트만을 내보낸다. 이후 Base64등으로 문자화 하자. */
	public static byte[] encrypt(SecretKey key, String str){
		try {
			Cipher cipher = Cipher.getInstance(DESede_STRING_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = str.getBytes("UTF-8");
			return cipher.doFinal(plainText);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/** 쓸일이 있을까? */
	public static SealedObject encrypt(SecretKey key, Serializable dataObj){
		try {
			Cipher cipher = Cipher.getInstance(DESede_STRING_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return  new SealedObject(dataObj, cipher);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String decryptText(SecretKey key, String str){
		try {
			Cipher cipher = Cipher.getInstance(DESede_STRING_MODE);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedText = cipher.doFinal(Strings.getByte(str));
			return new String(decryptedText, "UTF8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T decrypt(SecretKey key, SealedObject sealedObject){
		try {
			Cipher cipher = Cipher.getInstance(DESede_STRING_MODE);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return (T)sealedObject.getObject(cipher);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/** in을 암호화 해서 out을 만든다. */
	public static void encrypt(SecretKey key, File in, File out){
		FileInputStream ins = null;
		FileOutputStream outs = null;
		try {
			ins = new FileInputStream(in);
			outs = new FileOutputStream(out);
			encrypt(key,ins,outs);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				if(ins !=null) ins.close();
				if(outs !=null) outs.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/** in을 복호화 해서 out을 만든다. */
	public static void decrypt(SecretKey key, File in, File out){
		FileInputStream ins = null;
		FileOutputStream outs = null;
		try {
			ins = new FileInputStream(in);
			outs = new FileOutputStream(out);
			decrypt(key,ins,outs);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			try {
				if(ins !=null) ins.close();
				if(outs !=null) outs.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/** 암호화에 사용되는 버퍼 */
	private static final int FILE_BUFFER = 2048;	

	private static void encrypt(SecretKey key, InputStream in, OutputStream out) throws Exception {
		// Create and initialize the encryption engine
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		// Create a special output stream to do the work for us
		CipherOutputStream cos = new CipherOutputStream(out, cipher);

		// Read from the input and write to the encrypting output stream
		byte[] buffer = new byte[FILE_BUFFER];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			cos.write(buffer, 0, bytesRead);
		}
		cos.close();

		// For extra security, don't leave any plaintext hanging around memory.
		java.util.Arrays.fill(buffer, (byte) 0);
	}

	public static void decrypt(SecretKey key, InputStream in, OutputStream out) throws Exception {
		// Create and initialize the decryption engine
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE, key);

		// Read bytes, decrypt, and write them out.
		byte[] buffer = new byte[FILE_BUFFER];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(cipher.update(buffer, 0, bytesRead));
		}

		// Write out the final bunch of decrypted bytes
		out.write(cipher.doFinal());
		out.flush();
	}
	
	

	/** TripletDes방식으로 File을 암호화 한다. 왜안되지? ㅠ */
	/*@Deprecated
	public static void encrypt(File org, String key, File crypted) {
		java.io.FileInputStream in = null;
		java.io.FileOutputStream fileOut = null;
		javax.crypto.CipherOutputStream out = null;
		try {
			Cipher cipher = buildCipher(Cipher.ENCRYPT_MODE, key);
			in = new java.io.FileInputStream(org);
			fileOut = new java.io.FileOutputStream(crypted);
			out = new javax.crypto.CipherOutputStream(fileOut, cipher);
			byte[] buffer = new byte[FILE_CRYPT_BUFFER];
			int length;
			while ((length = in.read(buffer)) != -1)
				out.write(buffer, 0, length);
			java.util.Arrays.fill(buffer, (byte) 0);
			// while((length = in.read()) != -1) out.write(length);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (in != null)
					in.close();
				if (fileOut != null)
					fileOut.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}	*/
	
	/** TripletDes방식으로 File을 암호화 한다. 왜안되지? ㅠ */
	/** TripletDes방식으로 File을 복호화 한다. */
	/*public static void decrypt(File crypted, String key, File org) {
		java.io.FileInputStream in = null;
		java.io.FileOutputStream fileOut = null;
		javax.crypto.CipherOutputStream out = null;
		try {
			Cipher cipher = buildCipher(Cipher.DECRYPT_MODE, key);
			in = new java.io.FileInputStream(crypted);
			fileOut = new java.io.FileOutputStream(org);
			out = new javax.crypto.CipherOutputStream(fileOut, cipher);
			byte[] buffer = new byte[FILE_CRYPT_BUFFER];
			int length;
			while ((length = in.read(buffer)) != -1)
				out.write(buffer, 0, length);
			// while((length = in.read()) != -1) out.write(length);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (in != null)
					in.close();
				if (fileOut != null)
					fileOut.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}	*/
	
	/**
	 * 암호화한 String을 char int 형으로 분리한다. 테스트 위해 제작
	 */
	/*public static String encryptInt(String baseStr, String key) {
		StringBuilder b = new StringBuilder();
		Latch l = new Latch();
		for (char ch : encrypt(baseStr, key).toCharArray()) {
			if (!l.next())
				b.append(",");
			b.append((int) ch);
		}
		return b.toString();
	}*/

	/**
	 * char int 형으로 분리된 문자열을 복호화 한다. 테스트 위해 제작
	 */
	/*public static String decryptInt(String baseStr, String key) {
		StringBuilder b = new StringBuilder();
		for (String ch : baseStr.split(",")) {
			b.append((char) Integer.parseInt(ch));
		}
		baseStr = b.toString();
		return decrypt(baseStr, key);
	}*/

}