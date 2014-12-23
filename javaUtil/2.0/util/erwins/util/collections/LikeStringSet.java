package erwins.util.collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.SpringUtil;

/** 
 * 단어들(긴 단어)을 메모리에 미리 넣어놓고, 키워드들(짧은 단어)를 매칭할때 사용된다.
 * delegate로 새로 만들자.
 *  
 *  LikeStringSet map = new LikeStringSet();
		map.add("값싼청바지");
		map.add("엄청싼청바지");
		map.add("블루청바지");
		map.add("값싼모조장비");
		map.add("값싼키보드");
		map.add("값싼키위");
		
		System.out.println(map.contains("바지"));
		System.out.println(map.contains("싼청바지"));
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
