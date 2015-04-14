package erwins.util.lib;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.util.PathMatcher;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;



/**
 * 간단비교 유틸 
 * 참고소스 : NameFileComparator 
 * @author sin
 */
public abstract class CompareUtil{
	
	/** 키값 문자열사이즈의 역순정렬 */
	@SuppressWarnings("rawtypes")
	public static Comparator<Entry> ENTRY_KEY_LENGTH = new Comparator<Entry>() {
		@Override
		public int compare(Entry o1, Entry o2) {
			int a = o1.getKey().toString().length(); 
			int b = o2.getKey().toString().length();
			return Ints.compare(a, b);
		}
	};
	
	/** 역순 Comparator를 리턴한다. */
	public static <T> Comparator<T> rev(final Comparator<T> org){
		return new Comparator<T>() {
			@Override
			public int compare(T arg0, T arg1) {
				int compare = org.compare(arg0, arg1);
				return compare * -1;
			}
		};
	}
	
	public static Comparator<Object> STRING_LENGTH = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			int a = o1==null ? 0 : o1.toString().length(); 
			int b = o2==null ? 0 :o2.toString().length();
			return Ints.compare(a, b);
		}
	};
	
	/** 스트링 역순 비교자. Serializable때문에 이렇게 작성 */
	@SuppressWarnings("serial")
	@Deprecated
	public static class StringRevComparator implements Serializable,Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2)*-1;
		}
	}
	
	/**
	 *  toString() 으로 비교한다.
	 * Serializable때문에 이렇게 작성 */
	@SuppressWarnings("serial")
	public static class StringComparator<T> implements Serializable,Comparator<T>{
		@Override
		public int compare(T o1, T o2) {
			if(o1==null || o2==null) return 0;
			return o1.toString().compareTo(o2.toString());
		}
	}
	
	
	@SuppressWarnings("serial")
	public static class IntegerComparator implements Serializable,Comparator<Integer>{
		@Override
		public int compare(Integer o1, Integer o2) {
			return Ints.compare(o1, o2);
		}
	}
	
	/** 스트링 역순 비교자 */
    public static Comparator<String> STRING_REV = new StringRevComparator();
    
    /**
     * equals인데 널 무시
     */
    public static boolean nullSafeEquals(Object a,Object b){
        if(a==null || b==null) return false;
        return a.equals(b);
    }
    
    /**
     * compareTo인데 널 무시
     * 여러개 해야할 수 있음으로 int를 리턴한다.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T extends Comparable> int nullSafeCompare(T a,T b,boolean asc){
        if(a==null || b==null) return 0;
        return a.compareTo(b) * (asc ? 1 : -1);
    }

    /** List로 된 Map을 정렬한다.  그루비 땜에 남겨둔다. */
	@SuppressWarnings("rawtypes")
	public static Comparator<Map> mapComparator(final String key) {
		Comparator<Map> c = new Comparator<Map>() {
			@Override
			public int compare(Map o1, Map o2) {
				return o1.get(key).toString().compareTo(o2.get(key).toString());
			}
		};
		return c;
	}
	
	/** Groovy등에서 사용 */
	@SuppressWarnings("rawtypes")
	public static Comparator<Map> mapComparator(final List<String> keys) {
		Comparator<Map> c = new Comparator<Map>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Map o1, Map o2) {
				int compare = 0;
				for(String key : keys){
					Comparable e1 = (Comparable)o1.get(key);
					Comparable e2 = (Comparable)o2.get(key);
					if(e1==null && e2==null) compare = 0;
					else if(e1==null) compare = 1;
					else if(e2==null) compare = -1;
					else compare = e1.compareTo(e2);
					if(compare!=0) break;
				}
				return compare;
			}
		};
		return c;
	}
	
    
    /** null safe한 isEmpty() */
    public static boolean isEmpty(Collection<?> list){
    	if(list==null) return true;
    	return list.isEmpty();
    }
    
	/** null safe한 isEmpty() */
	public static <T> boolean isEmpty(T[] array) {
		if (array == null || array.length == 0) return true;
		return false;
	}
	
    /** null safe한 isEmpty() */
    public static boolean isEmptyObject(Object obj){
    	if(obj==null) return true;
    	if(obj instanceof String) return Strings.isNullOrEmpty((String)obj);
    	if(obj instanceof Collection) return isEmpty((Collection<?>)obj);
    	if(obj.getClass().isArray()) return isEmpty((Object[])obj);
    	return false;
    }
	
	/** 
	 * 많은 패턴을 매치해서 하나라고 매칭이 되는지?
	 * 귀찮아서 한방에 해결하게 만듬 
	 *  */
	public static boolean isMatchAny(PathMatcher matcher,Collection<String> patterns,Collection<String> paths) {
		for(String path : paths){
			for(String pattern : patterns){
				if(matcher.match(pattern, path)) return true;
			}
		}
		return false;
	}
	
    
    /**
     * DB값을 더티체크할 목적으로 만들었다. 
     * 둘다 널이면 true를 리턴 */
    public static <T> boolean isEqualIgnoreNullEmptyIncludeFilds(T a,T b,Collection<String> includes,Collection<String> excludes){
    	Map<String,Field> map = ReflectionUtil.getAllDeclaredFieldMap(a.getClass());
    	if(isEmpty(includes)) includes = map.keySet();
    	if(!isEmpty(excludes)) includes.removeAll(excludes);
    	for(String key : includes){
    		Field field = map.get(key);
    		Object aValue = ReflectionUtil.getField(field, a);
    		Object bValue = ReflectionUtil.getField(field, b);
    		Class<?> type = field.getType();
    		if(String.class.isAssignableFrom(type)){
    			boolean isEqual = isEqualIgnoreNullEmpty((String)aValue,(String)bValue);
    			if(!isEqual) return false;
    		}else{
    			boolean isEqual = isEqualIgnoreNull(aValue,bValue);
    			if(!isEqual) return false;
    		}
    	}
    	return true;
    }
    
    

	/**
	 * 배열에 null이 있는지 확인한다. 하나라도 있으면 true를 리턴한다. 배열의 size가 0이면 false이다.
	 */
	public static boolean isNullAny(Object ... items) {
		for (Object item : items) if (item == null) return true;
		return false;
	}
	
	/** ... 파라메터가 어레이를 구분하지 못하는 오류를 방지하기위해 이름 변경 */
	public static <T> boolean isNullItemAny(Collection<T>  items) {
		for (T item : items) if (item == null) return true;
		return false;
	}
	
	/** ... 파라메터가 어레이를 구분하지 못하는 오류를 방지하기위해 이름 변경 */
	public static <T> boolean isNullItemAny(T[]  items) {
		for (T item : items) if (item == null) return true;
		return false;
	}
    
    //===================================================== 동등 비교 ==================================================
    
    /** null이면 false이다. 하나라도 같으면 true를 리턴한다.
     * 주의! T가 Object로 분류될수도 있다. 컴파일러가 이를 잡아내지 못한다. */
	public static <T> boolean isEqualsAny(T body, T ... items) {
		if(body==null) return false;
		if(body instanceof Collection) throw new IllegalArgumentException("body에는 Collection이 들어오면 안됩니다. isEqualsCollectionAny를 사용해 주세요.");
		if (isEmpty(items)) return false;
		for (T item : items) if (item.equals(body)) return true;
		return false;
	}
	
	/** null이면 false이다. 하나라도 같으면 true를 리턴한다. */
	public static <T> boolean isEqualsAny(T body, Collection<T> items) {
		if(body==null) return false;
		if (isEmpty(items)) return false;
		for (T item : items) if (item.equals(body)) return true;
		return false;
	}

	/**
	 * ==으로 비교한다. ????
	 */
	public static <T> boolean isSameAny(T body, T... items) {
		if (body == null || items.length == 0) return false;
		for (T item : items)
			if (body == item) return true;
		return false;
	}
	
	/** null이면 false이다. 하나라도 같으면 true를 리턴한다. */
	public static <T> boolean isEqualsCollectionAny(Collection<T> a, Collection<T> b) {
		if(isNullAny(a,b)) return false;
		for (T each : a) if (isEqualsAny(each,b)) return true;
		return false;
	}

	
	/** 두개의 리스트가 틀린지? equals()의 오버라이드 비교에 사용된다.
	 *  두 컬렉션이 null이라면 같다고 비고한다. */
    public static <T extends Comparable<T>> boolean isEqualCollectionDataNullSafe(List<T> a , List<T> b){
        if(a==null && b==null) return true;
        else if(a==null && b!=null) return false;
        else if(a!=null && b==null) return false;
        return isEqualCollectionData(a,b);
    }

    /** 두 리스트의 데이터(DB에서 가져온 데이터 등) 비교가 모두 같을경우 true
     * 내용물중 null이라면 오류.. (수정?)  */
    public static <T extends Comparable<T>> boolean isEqualCollectionData(List<T> a , List<T> b){
        if(a==null || b==null) return false;
        if(a.size() != b.size()) return false;
        int size = a.size();
        for(int i=0;i<size;i++){
            T aa = a.get(i);
            T bb = b.get(i);
            if(aa.compareTo(bb) != 0) return false;
        }
        return true;
    }
    
    /** 두 리스트의 데이터(DB에서 가져온 데이터 등) 비교가 모두 같을경우 true */
    public static <T> boolean isEqualCollectionData(List<T> a , List<T> b,Comparator<T> comparator){
        if(a==null || b==null) return false;
        if(a.size() != b.size()) return false;
        int size = a.size();
        for(int i=0;i<size;i++){
            T aa = a.get(i);
            T bb = b.get(i);
            if(comparator.compare(aa, bb) != 0) return false;
        }
        return true;
    }
    
    /** 널과 Empty는 동등하게 취급. 
     *  둘다 널이면 true를 리턴 */
    public static boolean isEqualIgnoreNullEmpty(String a,String b){
    	boolean aEmpty = Strings.isNullOrEmpty(a);
    	boolean bEmpty = Strings.isNullOrEmpty(b);
    	if(aEmpty && bEmpty) return true;
    	else if(!aEmpty && !bEmpty) return a.equals(b);
    	else  return false;
    }
    
    /**  둘다 널이면 true를 리턴 */
    public static <T> boolean isEqualIgnoreNull(T a,T b){
    	boolean aEmpty = a == null;
    	boolean bEmpty = b == null;
    	if(aEmpty && bEmpty) return true;
    	else if(!aEmpty && !bEmpty) return a.equals(b);
    	else  return false;
    }
    
    /** 커먼즈에 있는거 제너릭이 안되서 걍 하나 만듬. */
    public static class ComparatorChain<T> implements Comparator<T>{

    	private final Iterable<Comparator<T>> comparatorSet;
    	
    	public ComparatorChain(Iterable<Comparator<T>> comparatorSet){
    		this.comparatorSet = comparatorSet;
    	}
    	
		@Override
		public int compare(T o1, T o2) {
			for(Comparator<T> each : comparatorSet){
				int compare = each.compare(o1, o2);
				if(compare!=0) return compare;
			}
			return 0;
		}
    	
    }

    /** List를 소팅할때 사용된다.
     * ex) OrderBy<Ad> sort1 = new OrderBy<Ad>().desc("impRank").desc("cpc"); */
    public static class OrderBy<T> implements Comparator<T>{

    	@SuppressWarnings("unchecked")
		private Map<String,Boolean> orderInfo = new ListOrderedMap();
    	private Map<String,Field> fieldMap;
    	
		@Override
		public int compare(T o1, T o2) {
			if(fieldMap==null) fieldMap =  ReflectionUtil.getAllDeclaredFieldMap(o1.getClass());
			int order = 0;
			for(Entry<String, Boolean> entry : orderInfo.entrySet()){
				Field field = fieldMap.get(entry.getKey());
				Comparable<?> v1 = (Comparable<?>) ReflectionUtil.getField(field, o1);
				Comparable<?> v2 = (Comparable<?>) ReflectionUtil.getField(field, o2);
				order = CompareUtil.nullSafeCompare(v1, v2, entry.getValue());
				if(order!=0) return order;
			}
			return order;
		}
		
		public OrderBy<T> asc(String name){
			orderInfo.put(name, Boolean.TRUE);
			return this;
		}
		public OrderBy<T> desc(String name){
			orderInfo.put(name, Boolean.FALSE);
			return this;
		}
    	
    }
    
    

}
