package erwins.util.lib.security;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import erwins.util.lib.FileUtil;

/**
 * 비대칭키. 부분 확인. 확장 후 싱글톤으로 운영하자.
 */
public class CryptorAsymmetry {

	//private static final int bufSize = 2048;
	public static final String PRIVATE_KEY_FILE_NAME = "privateKey.ser";
	public static final String PUBLIC_KEY_FILE_NAME = "publicKey.ser";

	private PrivateKey privateKey;
	private PublicKey publicKey;
	private File root;

	/** root에 키들이 있으면 자동 로드. */
	public CryptorAsymmetry(File root) {
		this.root = root;
		File privateKeyFile = new File(root, PRIVATE_KEY_FILE_NAME);
		File publicKeyFile = new File(root, PUBLIC_KEY_FILE_NAME);
		if (privateKeyFile.exists() && publicKeyFile.exists()) {
			privateKey = FileUtil.getObject(privateKeyFile);
			publicKey = FileUtil.getObject(publicKeyFile);
		}
	}

	/** 키 생성하고 파일로 저장한다. */
	public void generateKey() {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			KeyPair kp = kpg.genKeyPair();

			privateKey = kp.getPrivate();
			publicKey = kp.getPublic();

			File privateKeyFile = new File(root, PRIVATE_KEY_FILE_NAME);
			File publicKeyFile = new File(root, PUBLIC_KEY_FILE_NAME);

			FileUtil.setObject(privateKeyFile, privateKey);
			FileUtil.setObject(publicKeyFile, publicKey);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] encrypt(byte[] message){
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			cipher.update(message);
			return cipher.doFinal();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public byte[] decrypt(byte[] message){
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			cipher.update(message);
			return cipher.doFinal();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	public void encryptFile(File in, File out){

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			FileInputStream fis = new FileInputStream(in);

			FileOutputStream fos = new FileOutputStream(out);

			CipherOutputStream cos = new CipherOutputStream(fos, cipher);

			byte[] buffer = new byte[bufSize];

			int length;

			while ((length = fis.read(buffer)) != -1)

				cos.write(buffer, 0, length);

			fis.close();

			fos.close();
			cos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	// 파일 복호화

	public void decryptFile(String in, String out) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		FileInputStream fis = new FileInputStream(in);

		FileOutputStream fos = new FileOutputStream(out);

		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		byte[] buffer = new byte[bufSize];

		int length;

		while ((length = fis.read(buffer)) != -1)
			cos.write(buffer, 0, length);

		fis.close();

		fos.close();

	}*/

}