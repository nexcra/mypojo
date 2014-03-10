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
 * set 버전과 동일.  이쪽은 걍 contains임...
 *  
 *  ikeStringMap map = new LikeStringMap();
		map.put("값싼블루청바지","123123");
		map.put("값싼청바지","123123");
		map.put("엄청싼청바지","123");
		map.put("블루청바지","123123");
		map.put("값싼모조장비","123");
		map.put("값싼키보드","123");
		map.put("값싼키위","123");
		
		System.out.println(map.contains("바지"));
		System.out.println(map.contains("싼청바지"));
		
		System.out.println(map.contains("값싼"));
		System.out.println(map.contains("값싼키"));
		
		System.out.println(map.contains("장비"));
		System.out.println(map.contains("모조"));
		System.out.println(map.contains("싼청"));
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
