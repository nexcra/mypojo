package erwins.util.guava;

import java.math.BigDecimal;

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

}
