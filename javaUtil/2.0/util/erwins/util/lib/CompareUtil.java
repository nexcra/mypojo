package erwins.util.lib;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		return new Comparator<T>(){
			@Override
			public int compare(T arg0, T arg1) {
				int compare = org.compare(arg0, arg1);
				return compare * -1;
			}
		};
	}
	
    /** 스트링 역순 비교자 */
    public static Comparator<String> STRING_REV = new Comparator<String>(){
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2)*-1;
        }
    };
    
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
	
    /** 두개의 리스트가 틀린지? equals()의 오버라이드 비교에 사용된다. null끼리는 같다고 비고한다. */
    public static <T extends Comparable<T>> boolean isEqualCollectionDataNullSafe(List<T> a , List<T> b){
        if(a==null && b==null) return true;
        else if(a==null && b!=null) return false;
        else if(a!=null && b==null) return false;
        return isEqualCollectionData(a,b);
    }
    

    /** 두 리스트의 데이터(DB에서 가져온 데이터 등) 비교가 모두 같을경우 true */
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
    
    

}
