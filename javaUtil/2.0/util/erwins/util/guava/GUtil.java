package erwins.util.guava;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;

public abstract class GUtil {
	
	public static final MapJoiner WEB_JOINER = Joiner.on('&').withKeyValueSeparator("=");
	public static final MapSplitter WEB_SPLITTER = Splitter.on('&').omitEmptyStrings().trimResults().withKeyValueSeparator('=');
	
	public static final Joiner COMMMA_JOINER = Joiner.on(',').skipNulls();
	public static final Splitter COMMMA_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();
	
	public static final Function<String,String> TO_LOWER_CASE = new Function<String,String>(){
		@Override
		public String apply(String input) {
			if(input==null) return "";
			return input.toLowerCase();
		}
	};
	public static final Function<String,String> ESCAPE_W_SPACE = new Function<String,String>(){
		@Override
		public String apply(String input) {
			if(input==null) return "";
			return input.replaceAll(" ", "");
		}
	};

}
