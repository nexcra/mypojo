package erwins.util.text;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Charsets;

/**
 * ThreadSafe 한지 확인되지 않았음으로 걍 만들어 쓸것
 **/
public class Base64s{
	
	private Charset defaultset = Charsets.UTF_8;

	public void setDefaultset(Charset defaultset) {
		this.defaultset = defaultset;
	}
	
	public String encode(String str){
		return new String(Base64.encodeBase64(str.getBytes(defaultset)),defaultset);
	}
	
	public String decode(String str){
		return new String(Base64.decodeBase64(str.getBytes(defaultset)),defaultset);
	}
	
	
	

}