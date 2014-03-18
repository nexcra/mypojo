package erwins.util.guava;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;

import erwins.util.lib.ReflectionUtil;

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

}
