package erwins.util.lib;

import java.util.Comparator;



/**
 * 간단비교 유틸 
 * @author sin
 */
public abstract class CompareUtil{

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
    

}
