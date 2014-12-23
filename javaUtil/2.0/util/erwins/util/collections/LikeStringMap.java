package erwins.util.collections;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.SpringUtil;

/** 
 * 단어들(긴 단어)을 메모리에 미리 넣어놓고, 키워드들(짧은 단어)를 매칭할때 사용된다.
 * delegate로 새로 만들자.
 *   */
@NotThreadSafe
public class LikeStringMap<T> extends AbstractMapSupport<String,T>{
	
	private Multimap<String,HashEntry<T>> contains = HashMultimap.create();
	private Map<String,T> map = Maps.newHashMap();
	private int minLength = 2;
	
	@Override
	public T put(String key,T value){
		List<String> words = SpringUtil.splitWord(key,minLength);
		for(String each : words){
			contains.put(each,new HashEntry<T>(key,value));
		}
		return map.put(key,value);
	}
	
	@Override
	public T get(Object key) {
		return map.get(key);
	}
	
	public Collection<HashEntry<T>> contains(String value){
		return contains.get(value);
	}
    

}
