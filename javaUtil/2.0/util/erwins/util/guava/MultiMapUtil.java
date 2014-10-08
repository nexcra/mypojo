package erwins.util.guava;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.core.convert.converter.Converter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.internal.Maps;

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
    
	/** List를 Map을 변경해준다. groovy에 있는거 참고
	 * 아래의 멀티맵  create와 유사하다.
	 * CollectionUtil보다 여기가 적합해 보여서 여기둔다.
	 * function으로 변환되는 자료는 유니크 해야한다. */
    public static <K,V> Map<K,V> toMap(List<V> list,Function<V, K> function) {
    	Map<K,V> map = Maps.newHashMap();
    	for(V each : list){
    		K key = function.apply(each);
    		V exist = map.put(key, each);
    		Preconditions.checkState(exist==null, "key값은 유일해야합니다. " + key.toString());
    	}
		return map;
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
    
    /** map의 키 순서대로 사용하고 싶을때.
     * 즉 ListOrderedMap 가 구현체 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K,V> Map<K,List<V>> toListOrderedMap(Multimap<K, V> multiMap,Comparator<K> comparator){
    	List<K> keys = Lists.newArrayList();
		keys.addAll(multiMap.keySet());
		if(comparator==null) Collections.sort((List<Comparable>)keys);
		else Collections.sort(keys,comparator);
		Map<K,Collection<V>> asMap =  multiMap.asMap();
		ListOrderedMap map = new ListOrderedMap();
		for(K key : keys){
			map.put(key, asMap.get(key));
		}
		return map;
    }
    
    /** 
     * 생성자를 단순화한것. 인라인으로 쓰기위해 만들었다
     * ex) Multimap<Long, CcrFilterAdInfoVo> groupByAccId = MultiMapUtil.create(items, CcrFilterAdInfoVo.TO_PK);
     *  */
    public static <PK,VO> ArrayListMultimap<PK,VO> create(List<VO> list,Converter<VO,PK> converter) {
    	ArrayListMultimap<PK,VO> map = ArrayListMultimap.create();
    	for(VO each : list){
    		map.put(converter.convert(each), each);
    	}
    	return map;
    }

}
