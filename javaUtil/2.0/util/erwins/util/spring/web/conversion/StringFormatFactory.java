package erwins.util.spring.web.conversion;

import java.util.Locale;
import java.util.Set;

import org.springframework.expression.ParseException;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import com.google.common.collect.Sets;

import erwins.util.lib.ReflectionUtil;

/** 어노테이션을 붙이면 컨버팅시 자동으로 처리해준다. */
public class StringFormatFactory implements AnnotationFormatterFactory<StringFormat> {

	private static final Set<Class<?>> TYPE = Sets.<Class<?>>newHashSet(String.class);
	
	@Override
	public Set<Class<?>> getFieldTypes() {
		return TYPE;
	}

	/** 역변환은 불가능하다. 순서대로 적용 */
	@Override
	public Parser<String> getParser(final StringFormat anno, Class<?> arg1) {
		return new Parser<String>() {
			@Override
			public String parse(String text, Locale locale) throws ParseException, java.text.ParseException {
				String changed = text;
				for(Class<? extends Parser<String>> clazz : anno.value()){
					Parser<String> parser = ReflectionUtil.newInstance(clazz);
					changed = parser.parse(changed, locale);
				}
				return changed;
			}
		};
	}

	@Override
	public Printer<?> getPrinter(StringFormat anno, Class<?> arg1) {
		return ConversionSet.PASS_THROUGH_PRINTER;
	}


	
}
