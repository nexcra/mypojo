package erwins.util.guava;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import erwins.util.root.KeyValueVo;

public abstract class MultiMapUtil {
	
	/** map을 key,value의 List로 만들어준다. */
    public static List<KeyValueVo<Integer, String>> toList(Multimap<Integer, String> treeMap) {
		List<KeyValueVo<Integer, String>> ranked = Lists.newArrayList();
		for(Entry<Integer, Collection<String>> each :treeMap.asMap().entrySet()){
			for(String value : each.getValue()){
				KeyValueVo<Integer, String> vo = new KeyValueVo<Integer, String>();
				vo.setKey(each.getKey());
				vo.setValue(value);
				ranked.add(vo);
			}
		}
		return ranked;
	}
    
    /** 졍렬된 키를 리턴한다. 주로 해시멀티맵에서 사용 */
    public static <K extends Comparable<K>,V> List<K> sortrdKey(Multimap<K, V> multiMap){
    	List<K> keys = Lists.newArrayList();
		keys.addAll(multiMap.keySet());
		Collections.sort(keys);
		return keys;
    }
    
    /** 졍렬된 키를 리턴한다. 주로 해시멀티맵에서 사용 */
    public static <K,V> List<K> sortrdKey(Multimap<K, V> multiMap,Comparator<K> comparator){
    	List<K> keys = Lists.newArrayList();
		keys.addAll(multiMap.keySet());
		Collections.sort(keys,comparator);
		return keys;
    }

}
