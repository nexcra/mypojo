package erwins.util.collections;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.SpringUtil;

/** 
 * LikeStringMap 하고 비슷하지만, map구성 안에 서치할 항목이 들어가고, 비교인자로 문자텍스트가 온다. 
 * 이걸 더 늦게 만들었는데... 이게 더 많이 쓰이는듯 ㅠㅠ
 *   */
@NotThreadSafe
public class LikeStringMatchMap<T> extends AbstractMapSupport<String,T>{
	
	private Map<String,T> map = Maps.newHashMap();
	private int minLength = 2;
	
	@Override
	public T put(String key,T value){
		return map.put(key,value);
	}
	
	@Override
	public T get(Object key) {
		return map.get(key);
	}
	
	@Override
	public void clear() {
		map.clear();
	}
	
	/** HashEntry의 key는 매핑된 텍스트.   */
	public List<HashEntry<T>> matchAny(String query){
		List<HashEntry<T>> result = Lists.newArrayList();
		for(String subText : SpringUtil.splitWord(query,minLength)){
			T value = map.get(subText);
			if(value==null) continue;
			result.add(new HashEntry<T>(subText,value));
		}
		return result;
	}
	
	/** matchAny와 동일하나 뒷 like로 매칭된다.  %문자  */
	public List<HashEntry<T>> matchAnySuffix(String query){
		List<HashEntry<T>> result = Lists.newArrayList();
		for(String subText : SpringUtil.splitWordSuffix(query,minLength)){
			T value = map.get(subText);
			if(value==null) continue;
			result.add(new HashEntry<T>(subText,value));
		}
		return result;
	}
	
	/** matchAny와 동일하나 앞 like로 매칭된다.  문자%  */
	public List<HashEntry<T>> matchAnyPrefix(String query){
		List<HashEntry<T>> result = Lists.newArrayList();
		for(String subText : SpringUtil.splitWordPrefix(query,minLength)){
			T value = map.get(subText);
			if(value==null) continue;
			result.add(new HashEntry<T>(subText,value));
		}
		return result;
	}
	
	

	public int getMinLength() {
		return minLength;
	}

	public LikeStringMatchMap<T> setMinLength(int minLength) {
		this.minLength = minLength;
		return this;
	}
	
	
	


}
