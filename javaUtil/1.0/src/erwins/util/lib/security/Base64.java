package erwins.util.lib.security;

import com.oreilly.servlet.Base64Decoder;
import com.oreilly.servlet.Base64Encoder;

/**
 * 암호화 , 복호화, Base64, 해쉬화 , 해쉬값 검증 등의 작업 Base64란 2진 데이터를, 문자코드에 영향을 받지 않는 공통
 * ASCII 영역의 문자들로만 이루어진 일련의 문자열로 바꾸는 방식을 가리키는 개념으로서,간단히 64진수로 보면 된다. <br> 
 * 오라일리의 jar가 필요하다..
 * @author erwins(my.pojo@gmail.com)
 */
public abstract class Base64 {

	/**
	 * Base64로 인코딩한다.
	 * 
	 */
	public static String base64Encode(String str) {
		return Base64Encoder.encode(str.getBytes());
	}

	/**
	 * Base64를 디코딩한다.
	 */
	public static String base64Decode(String str) {
		return Base64Decoder.decode(str);
	}

}