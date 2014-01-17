package erwins.util.spring;

import org.springframework.core.convert.converter.Converter;

public abstract class SpringConversionUtil {
	
	/** 만만한거 변환할때 사용 */
	public static final Converter<Object,String> TO_STRING_DEFAULT = new Converter<Object, String>() {
		@Override
		public String convert(Object arg0) {
			if(arg0==null) return "";
			return arg0.toString();
		}
	};
    
}
