package erwins.util.spring;

import org.springframework.core.convert.converter.Converter;

/** 스프링 Converter 인터페이스의 구현체 모음집. */
public abstract class SpringConversions {
	
	/** 만만한거 변환할때 사용 */
	public static final Converter<Object,String> TO_STRING_DEFAULT = new Converter<Object, String>() {
		@Override
		public String convert(Object arg0) {
			if(arg0==null) return "";
			return arg0.toString();
		}
	};
    
}
