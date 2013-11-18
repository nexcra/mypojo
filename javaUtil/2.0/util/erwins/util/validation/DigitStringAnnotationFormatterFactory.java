package erwins.util.validation;

import java.util.Locale;
import java.util.Set;

import org.springframework.expression.ParseException;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;

/** 어노테이션을 붙이면 컨버팅시 자동으로 처리해준다. */
public class DigitStringAnnotationFormatterFactory implements AnnotationFormatterFactory<DigitString> {

	@Override
	public Set<Class<?>> getFieldTypes() {
		Set<Class<?>> aa = Sets.newHashSet();
		aa.add(String.class);
		return aa;
	}

	/** 역변환은 불가능하다. */
	@Override
	public Parser<String> getParser(DigitString arg0, Class<?> arg1) {
		return new Parser<String>() {
			@Override
			public String parse(String text, Locale locale) throws ParseException {
				return CharMatcher.DIGIT.retainFrom(text);
			}
		};
		
	}

	/** 숫자만 가져온다. */
	@Override
	public Printer<?> getPrinter(DigitString arg0, Class<?> arg1) {
		return null;
	}


	
}
