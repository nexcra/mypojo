package erwins.util.lib.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import erwins.util.root.exception.IORuntimeException;

/**
 * Cryptor 대신에 사용. AES만 쓰니까 새로 만들었다.
 * Base64자체가 스레드 비안전 함으로 Base64를 별도로 사용한다면 추가작업이 필요 
 */
public class AESCryptor {
	
	private static final String MODE = "AES/CBC/PKCS5Padding"; 
	private String encode = "UTF-8";
	/** 이놈은 알고리즘이 있어야 한다. */
	private IvParameterSpec ivSpec;
	/** 기본값 UTF-8  */
	public AESCryptor setEncode(String encode) {
		this.encode = encode;
		return this;
	}
	
	private SecretKey key;
	public SecretKey getKey() {
		return key;
	}
	
	
	public synchronized String encryptBase64(String str){
		byte[] raw = encrypt(str);
        try {
			return new String(Base64.encodeBase64(raw),encode);
		} catch (UnsupportedEncodingException e) {
			throw new IORuntimeException(e);
		}
	}
	
	private byte[] encrypt(String plainText){
		try {
			return encrypt(plainText.getBytes(encode));
		} catch (UnsupportedEncodingException e) {
			throw new IORuntimeException(e);
		}
	}
	
	private byte[] encrypt(byte[] org){
		try {
			Cipher cipher = Cipher.getInstance(MODE);
			cipher.init(Cipher.ENCRYPT_MODE, key,ivSpec);
			return cipher.doFinal(org);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/** RMI등에 사용된다. */
	public SealedObject encrypt(Serializable dataObj){
		try {
			Cipher cipher = Cipher.getInstance(MODE);
			cipher.init(Cipher.ENCRYPT_MODE, key,ivSpec);
			return  new SealedObject(dataObj, cipher);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String decrypt(byte[] data){
		try {
			Cipher cipher = Cipher.getInstance(MODE);
			cipher.init(Cipher.DECRYPT_MODE, key,ivSpec);
			byte[] decryptedText = cipher.doFinal(data);
			return new String(decryptedText, encode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized String decryptBase64(String base64){
		byte[] raw;
		try {
			raw = Base64.decodeBase64(base64.getBytes(encode));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
        return decrypt(raw);
	}
	
	/** RMI등에 사용된다. */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T decrypt(SecretKey key, SealedObject sealedObject){
		try {
			Cipher cipher = Cipher.getInstance(MODE);
			cipher.init(Cipher.DECRYPT_MODE, key,ivSpec);
			return (T)sealedObject.getObject(cipher);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/** in을 암호화 해서 out을 만든다. */
	public void encrypt(File in, File out){
		FileInputStream ins = null;
		FileOutputStream outs = null;
		try {
			ins = new FileInputStream(in);
			outs = new FileOutputStream(out);
			encrypt(ins,outs);
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
	public void decrypt(File in, File out){
		FileInputStream ins = null;
		FileOutputStream outs = null;
		try {
			ins = new FileInputStream(in);
			outs = new FileOutputStream(out);
			decrypt(ins,outs);
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

	private void encrypt(InputStream in, OutputStream out) throws Exception {
		// Create and initialize the encryption engine
		Cipher cipher = Cipher.getInstance(MODE);
		cipher.init(Cipher.ENCRYPT_MODE, key,ivSpec);

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

	public void decrypt(InputStream in, OutputStream out) throws Exception {
		// Create and initialize the decryption engine
		Cipher cipher = Cipher.getInstance(MODE);
		cipher.init(Cipher.DECRYPT_MODE, key,ivSpec);

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
	
	/* ================================================================================== */
	/*                              KEY                                                   */
	/* ================================================================================== */

	/** 이 key를 사용하면 String 복호화의 경우 padding오류가 난다?? 주의 */
	@Deprecated
	public AESCryptor generateKey(){
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance(MODE);
			key = keygen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/** 아무 문자나 막넣어도 키를 생성해준다. */
	public AESCryptor generateKeyByString(String stringForKey) {
		return generateKey(MD5.getHashHexString(stringForKey).substring(0,16));
	}
	
	/** 문자열 단일 암호화는 이것으로 생성하자. 영문자 16문자로 설정할 경우 128bit의 키 */
	public AESCryptor generateKey(String stringForKey) {
		byte[] keydata = stringForKey.getBytes();
		key = new SecretKeySpec(keydata, "AES");
		ivSpec = new IvParameterSpec(keydata);
		return this;
	}
	
	/** Key를 날로 저장한다. */
	public AESCryptor writeKeyForObject(File f){
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(key);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/** 걍 날로 저장한 Key를 읽어온다. */
	public AESCryptor readKeyForObject(File f){
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			key = (SecretKey)in.readObject();
			ivSpec = new IvParameterSpec(key.getEncoded());
	        in.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
}