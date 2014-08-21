package erwins.util.guava;

import com.google.common.collect.Range;

public abstract class RangeUtil {
	
	/** 영문표기를 더 간결하게 하기 위해서 추가 */
	public static <T extends Comparable<T>> Range<T> opneToInfinite(T endpoint){
		return (Range<T>) Range.greaterThan(endpoint);
	}
	
	/** 영문표기를 더 간결하게 하기 위해서 추가
	 * 중요!. 이력성 테이블에는 시작시간부터 정렬해서 이 메소드만 호출하면 된다. */
	public static <T extends Comparable<T>> Range<T> closeToInfinite(T endpoint){
		return (Range<T>) Range.atLeast(endpoint);
	}
	
	/** 영문표기를 더 간결하게 하기 위해서 추가 */
	public static <T extends Comparable<T>> Range<T> infiniteToOpen(T endpoint){
		return (Range<T>) Range.lessThan(endpoint);
	}
	
	/** 영문표기를 더 간결하게 하기 위해서 추가 */
	public static <T extends Comparable<T>> Range<T> infiniteToClose(T endpoint){
		return (Range<T>) Range.atMost(endpoint);
	}
    

}
