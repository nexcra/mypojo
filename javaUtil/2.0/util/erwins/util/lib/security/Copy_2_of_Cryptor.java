package erwins.util.lib.security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Cryptor 대신에 사용하자. 각종 옵션이 가능하도록 변경하였다.
 * ex) Cryptor c = new Cryptor().setMode(Mode.DESede).setEncode("euc-kr");
 * Base64 라이브러리를 sun패키지에서 apache껄로 바꿨다. 이게 더 나은듯.\
 * SecretKeySpec 과 IvParameterSpec는 생략했다.
 */
public class Copy_2_of_Cryptor {
	
	public static enum Mode{
		/** Advanced Encryption Standard
		 * 미국의 연방 표준 알고리즘으로서 20년이 넘게 사용되어 온 DES를 대신할 차세대 표준 알고리즘 */
		AES("AES/CBC/PKCS5Padding"),
		/** 일반 DES(Data Encryption Standard) */
		DES("DES/ECB/PKCS5Padding"),
		/** (트리플 DES) */
		DESede("DESede/ECB/PKCS5Padding");
		private final String cipherMode;
		
		private Mode(String cipherMode){
			this.cipherMode = cipherMode;
		}
		public String getCipherMode() {
			return cipherMode;
		}
	}
	
	/* ================================================================================== */
	/*                                getter / setter                                     */
	/* ================================================================================== */
	
	private Mode mode = Mode.DESede;
	/** 기본값  DESede*/
	public Copy_2_of_Cryptor setMode(Mode mode) {
		this.mode = mode;
		return this;
	}

	private String encode = "UTF-8";
	/** 기본값 UTF-8  */
	public Copy_2_of_Cryptor setEncode(String encode) {
		this.encode = encode;
		return this;
	}
	private SecretKey key;
	public SecretKey getKey() {
		return key;
	}
	
	public String encryptBase64(String str){
		byte[] raw = encrypt(str);
        try {
			return new String(Base64.encodeBase64(raw),encode);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** 바이트만을 내보낸다. 이후 Base64등으로 문자화 하자. */
	private byte[] encrypt(String str){
		try {
			Cipher cipher = Cipher.getInstance(mode.getCipherMode());
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = str.getBytes(encode);
			return cipher.doFinal(plainText);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/** RMI등에 사용된다. */
	public SealedObject encrypt(Serializable dataObj){
		try {
			Cipher cipher = Cipher.getInstance(mode.getCipherMode());
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return  new SealedObject(dataObj, cipher);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String decrypt(byte[] data){
		try {
			Cipher cipher = Cipher.getInstance(mode.getCipherMode());
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedText = cipher.doFinal(data);
			return new String(decryptedText, encode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String decryptBase64(String base64){
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
			Cipher cipher = Cipher.getInstance(mode.getCipherMode());
			cipher.init(Cipher.DECRYPT_MODE, key);
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
		Cipher cipher = Cipher.getInstance(mode.name());
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

	public void decrypt(InputStream in, OutputStream out) throws Exception {
		// Create and initialize the decryption engine
		Cipher cipher = Cipher.getInstance(mode.name());
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
	
	/* ================================================================================== */
	/*                              KEY                                                   */
	/* ================================================================================== */
	
	private KeySpec getKeySpec(byte[] keydata) throws InvalidKeyException {
		if(mode == Mode.DESede) return new DESedeKeySpec(keydata); 
		else if(mode == Mode.DES) return new DESKeySpec(keydata);
		else throw new RuntimeException("MODE not fount" + mode.name());
	}	

	/** 이 key를 사용하면 String 복호화의 경우 padding오류가 난다. 주의 */
	public Copy_2_of_Cryptor generateKey(){
		KeyGenerator keygen = null;
		try {
			keygen = KeyGenerator.getInstance(mode.name());
			key = keygen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/** 아무 문자나 막넣어도 키를 생성해준다. */
	public Copy_2_of_Cryptor generateKeyByString(String stringForKey) {
		return generateKey(MD5.getHashHexString(stringForKey).substring(0,26));
	}
	
	/** 문자열 단일 암호화는 이것으로 생성하자. ex)quantum.object@hotmail.com
	 * DESede's key length must be 26  */
	public Copy_2_of_Cryptor generateKey(String stringForKey) {
		byte[] keydata = stringForKey.getBytes();
		try {
			if(mode == Mode.AES){
				key = new SecretKeySpec(keydata, mode.name());
			}else{
				KeySpec keySpec = getKeySpec(keydata);
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(mode.name());
				key = keyFactory.generateSecret(keySpec);	
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/** TripleDES 전용? */
	public Copy_2_of_Cryptor writeKey(File f){
		try {
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(mode.name());
			
			byte[] rawkey = null;
			if(mode == Mode.DESede){
				DESedeKeySpec keyspec = (DESedeKeySpec) keyfactory.getKeySpec(key, DESedeKeySpec.class);
				rawkey = keyspec.getKey();
			}else if(mode == Mode.DES){
				DESKeySpec keyspec = (DESKeySpec) keyfactory.getKeySpec(key, DESKeySpec.class);
				rawkey = keyspec.getKey();
			}else throw new RuntimeException("MODE not fount" + mode.name());
			
			FileOutputStream out = new FileOutputStream(f);
			out.write(rawkey);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	/** Read a TripleDES secret key from the specified file */
	public Copy_2_of_Cryptor readKey(File f){
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			byte[] rawkey = new byte[(int) f.length()];
			in.readFully(rawkey);
			in.close();

			KeySpec keyspec = getKeySpec(rawkey);
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(mode.name());
			key = keyfactory.generateSecret(keyspec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	/** 걍 날로 저장한 Key를 읽어온다. */
	public Copy_2_of_Cryptor readKeyForObject(File f){
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			key = (SecretKey)in.readObject();
	        in.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
}