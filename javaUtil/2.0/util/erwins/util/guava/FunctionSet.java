package erwins.util.guava;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import erwins.util.lib.ReflectionUtil;

/** Function 모음집. */
public abstract class FunctionSet {
	
	/** 둘다 메이져 인터페이스다. 필요할때 스왑 */
	public static <A,B> Converter<A,B> toConverter(final Function<A,B> function){
		return new Converter<A,B>(){
			@Override
			public B convert(A a) {
				return function.apply(a);
			}
		};
	}
	
	/** 둘다 메이져 인터페이스다. 필요할때 스왑 */
	public static <A,B> Function<A,B> toFunction(final Converter<A,B> function){
		return new Function<A,B>(){
			@Override
			public B apply(A a) {
				return function.convert(a);
			}
		};
	}
	
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
	
	/** 리플렉션으로 추출한다. 간단한 통계 작업등에 사용하자.
	 * ex) Collections.max(FluentIterable.from(datas).transform(fieldFunction("m1Value",0L)).toSet()) */
	public static <T,K> Function<T,K> fieldFunction(final String name,final K defaultValue){
		return new Function<T,K>() {
			@Override
			public K apply(T vo) {
				K k = ReflectionUtil.findFieldValue(vo, name);
				if(k==null) k = defaultValue;
				return k;
			}
		};
	}
	/** 
	 * 제너릭 때문에 clazz를 받는다. 
	 * ex) NumberUtil.avg(FluentIterable.from(datas).transform(GUtil.fieldFunction("m5Value",Long.class)).toSet()) */
	public static <T,K> Function<T,K> fieldFunction(final String name,Class<K> clazz,final K defaultValue){
		return fieldFunction(name,defaultValue);
	}
	
	/** 위의 펑션필드를 이용한 간단 추출 샘플.
	 * toList() or 수정 가능한 List로 변경하려면 Lists.newArrayList() 을 사용하자 */
	public static <T,K> FluentIterable<K> fieldFunction(Iterable<T> it, final String name,final K defaultValue){
		return FluentIterable.from(it).transform(fieldFunction(name,defaultValue));
	}
	
	/** 맵에서 추출 */
	public static <K> Function<Map<K,Object>,String> mapToStringFunction(final K key){
		return new Function<Map<K,Object>, String>() {
			@Override
			public String apply(Map<K, Object> map) {
				Object v = map.get(key);
				return v==null ? null : v.toString();
			}
			
		};
	}

}
