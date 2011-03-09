package erwins.util.nio;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/** 스래드에 안전하지 않은듯? 인스턴스로 만들어주자. */
public class CharsetUtils{
	
	private final Charset charset = Charset.forName("UTF-8");
	private final CharsetDecoder decoder = charset.newDecoder();
	//private final CharsetDecoder decoder2 = Charset.forName("EUC-KR").newDecoder();
	//private final CharsetEncoder encoder = Charset.forName("EUC-KR").newEncoder();
	
	public String decode(ByteBuffer buffer){
		try {
			return decoder.decode(buffer).toString();
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}

	public Charset getCharset() {
		return charset;
	}
	
	/*
	public String decode2(ByteBuffer buffer){
		try {
			return decoder2.decode(buffer).toString();
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
	}*/
	
}