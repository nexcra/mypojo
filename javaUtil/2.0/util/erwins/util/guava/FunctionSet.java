package erwins.util.guava;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.google.common.base.Function;

/** Function 모음집. */
public abstract class FunctionSet {
	
	/** Groovy에서 CSV로 떨구기 위해 장만함 */
    public static final Function<Object,String> SQL_TO_STRING =  new Function<Object,String>() {
        @Override
        public String apply(Object input) {
            if(input==null) return "";
            if(input instanceof BigDecimal){
                BigDecimal in = (BigDecimal) input;
                return in.toPlainString();
            }
            return input.toString();
        }
    };
    
    /** 최대 길이만큼 자른다. */
    public static Function<String,String> substring(final int maxSize){
    	return new Function<String, String>() {
			@Override
			@Nullable
			public String apply(@Nullable String arg0) {
				if(arg0==null) return null;
				if(arg0.length() < maxSize) return arg0;
				return arg0.substring(0,maxSize);
			}
		};
    }

}
