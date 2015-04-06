package erwins.util.spring.web.conversion;

import java.util.Locale;

import org.springframework.expression.ParseException;
import org.springframework.format.Parser;

public class ToLowerCaseParser implements Parser<String> {

	@Override
	public String parse(String text, Locale locale) throws ParseException {
		if(text == null) return null;
		return text.toLowerCase(locale);
	}
	
}
