
package erwins.util.lib.security;

import java.io.File;

import javax.crypto.SecretKey;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import erwins.util.exception.Val;
import erwins.util.lib.Files;
import erwins.util.lib.Strings;
import erwins.util.tools.TextFile;

public class SecurityTest{
	
	private static File org = new File("/org.txt");
	private static File root = new File("/");
	
	@Test 
	public void cryptAsymmetryString(){
		CryptorAsymmetry a = new CryptorAsymmetry(root);
		a.generateKey();
		String org = "한국여 eng test @%^&*";
		byte[] crypted = a.encrypt(org.getBytes());
		String result = new String( a.decrypt(crypted));
		Val.isTrue(org.equals(result));
		Val.isTrue(new File(root,CryptorAsymmetry.PRIVATE_KEY_FILE_NAME).delete());
		Val.isTrue(new File(root,CryptorAsymmetry.PUBLIC_KEY_FILE_NAME).delete());
	}
	
	/** 일반 문자열을 암호화 / 복호하 한다. */
    @Test 
    public void cryptString(){
    	SecretKey key = Cryptor.generateKey("quantum.object@hotmail.com");
    	String org = "한국여 eng test @%^&*";
    	String sealed = Cryptor.encryptText(key, org);
    	String result = Cryptor.decryptText(key, sealed);
    	Val.isTrue(org.equals(result));
    }
    
    @Test 
    public void cryptFile1(){
    	SecretKey initKey = Cryptor.generateKey();
    	File keyFile = new File("/fileKey");
    	Cryptor.writeKey(initKey, keyFile);
    	SecretKey key = Cryptor.readKey(keyFile);  //키 읽기 쓰기 테스트.
    	File sealed = new File("/sealed.txt");
    	Cryptor.encrypt(key, org, sealed);
    	
    	File result = new File("/result.txt");
    	Cryptor.decrypt(key, sealed, result);
    	
    	Val.isTrue(TextFile.read(org).toString().equals(TextFile.read(result).toString()));
    	Val.isTrue(sealed.delete());
    	Val.isTrue(result.delete());
    	Val.isTrue(keyFile.delete());
    }
	
    @Test 
    public void cryptFile2(){
    	SecretKey key = Cryptor.generateKey();
    	//zip으로 묶은 후 테스트
    	File zip = new File("/zip");
    	File sealedZip = new File("/sealedZip");
    	File unsealedZip = new File("/unsealedZip");
    	File unzip = new File("/org(1).txt");
    	Files.zip(zip, org);
    	Cryptor.encrypt(key, zip, sealedZip);
    	Cryptor.decrypt(key, sealedZip, unsealedZip);
    	Files.unzip(unsealedZip);
    	
    	Val.isTrue(TextFile.read(org).toString().equals(TextFile.read(unzip).toString()));
    	Val.isTrue(zip.delete());
    	Val.isTrue(sealedZip.delete());
    	Val.isTrue(unsealedZip.delete());
    	Val.isTrue(unzip.delete());
    }
    
    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
    	StringBuilder buff = new StringBuilder();
    	for(int i=0;i<10000;i++){
    		buff.append("qweqwe");
    		buff.append("한글*^%$");
    		buff.append(i*i);
    		buff.append("\n");
    	}
    	Files.writeStr(buff,org);
    	Val.isTrue(org.length() > 200000L);
	}
    
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Val.isTrue(org.delete());
	}
    
    /** 암호화시 참고. 특수 문자의 경우 기본 제공되는 getByte()가 변형되어서 나온다. */
    @Test    
    public void getByte(){
        byte[] aa = "123￵ﾗ￈￼ﾞﾠS ".getBytes();
        byte[] bb = Strings.getByte("123￵ﾗ￈￼ﾞﾠS ");
        
        int sumA = 0;
        int sumB = 0;
        for (byte a : aa) sumA += a;        
        for (byte b : bb) sumB += b;
        
        Val.isTrue(!aa.equals(bb));
        Val.isTrue(sumA != sumB);
    }

}
