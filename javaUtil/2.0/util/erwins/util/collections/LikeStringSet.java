package erwins.util.collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.SpringUtil;

/** 앞뒤 like인 자료를 리턴한다. 10만건 내외 자료를 매우 빠르게 탐색할 용도로 사용한다.
 *  양이 많으면 쓰면 안된다.
 *  
 *  LikeStringSet map = new LikeStringSet();
		map.add("값싼청바지");
		map.add("엄청싼청바지");
		map.add("블루청바지");
		map.add("값싼모조장비");
		map.add("값싼키보드");
		map.add("값싼키위");
		
		System.out.println(map.endsWith("바지"));
		System.out.println(map.endsWith("싼청바지"));
		
		System.out.println(map.startsWith("값싼"));
		System.out.println(map.startsWith("값싼키"));
 *   */
@NotThreadSafe
public class LikeStringSet extends AbstractSetSupport<String>{
	
	private Multimap<String,String> contains = HashMultimap.create();
	private Set<String> set= Sets.newHashSet();
	private int minLength = 2;
	
	@Override
	public boolean add(String key){
		List<String> words = SpringUtil.splitWord(key,minLength);
		for(String each : words){
			contains.put(each,key);
		}
		return set.add(key);
	}
	
	@Override
	public boolean contains(Object value){
		return set.contains(value);
	}
	
	public Collection<String> contains(String value){
		return contains.get(value);
	}
	


}
