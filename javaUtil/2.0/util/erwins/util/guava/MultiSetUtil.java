package erwins.util.guava;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public abstract class MultiSetUtil {
	
    /** 
     * 멀티셋을 역변환한 멀티맵으로 변경해준다. Tree를 사용하면 value값으로 정렬이 가능하다. (최대치 3개까지 구할때 등)
     * ex) Multimap<Integer,String> treeMap = MultiSetUtil.reverse(matchedCategory,TreeMultimap.create(CompareUtil.rev(new IntegerComparator()),new StringComparator<String>()));
     *  */
    public static <K> Multimap<Integer, K> reverse(Multiset<K> matchedCategory,Multimap<Integer, K> out) {
		for(K key : matchedCategory.elementSet()){
			out.put(matchedCategory.count(key), key);
		}
		return out;
	}
    

}
