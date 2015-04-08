package erwins.util.spring.web.conversion;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.expression.ParseException;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import com.google.common.collect.Sets;

import erwins.util.spring.web.conversion.PatternFormat.PatternFormatType;
import erwins.util.text.RegEx;

public class PatternFormatFactory implements AnnotationFormatterFactory<PatternFormat> {

	private static final Set<Class<?>> TYPE = Sets.<Class<?>>newHashSet(String.class);
	
	@Override
	public Set<Class<?>> getFieldTypes() {
		return TYPE;
	}

	/** 역변환은 불가능하다. 순서대로 적용 */
	@Override
	public Parser<String> getParser(final PatternFormat anno, Class<?> arg1) {
		return new Parser<String>() {
			@Override
			public String parse(String text, Locale locale) throws ParseException, java.text.ParseException {
				Pattern pattern =  Pattern.compile(anno.regexp());
				if(anno.type() == PatternFormatType.include){
					return RegEx.retainFrom(pattern, text);
				}else if(anno.type() == PatternFormatType.exclude){
					return RegEx.replace(pattern, text, "");
				}else throw new IllegalStateException();
			}
		};
	}

	@Override
	public Printer<?> getPrinter(PatternFormat anno, Class<?> arg1) {
		return ConversionSet.PASS_THROUGH_PRINTER;
	}


	
}
