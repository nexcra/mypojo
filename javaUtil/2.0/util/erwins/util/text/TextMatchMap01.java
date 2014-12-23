package erwins.util.text;

import java.util.Collection;
import java.util.Set;

import lombok.experimental.Delegate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


/** 
 * 텍스트분석 샘플저장
 * 동일한 글자가 모두 포함되어 있으나 위치만 다른거 찾기. 일단 길이는 무시
 * ex) 샴푸염색 -> 염색샴푸
 * */
public class TextMatchMap01{

	@Delegate
	private Multimap<Set<Character>,String> map = ArrayListMultimap.create();
	
	public TextMatchMap01(Collection<String> list){
    	for(String keyword : list){
    		Set<Character> set = StringUtil.toCharSet(keyword);
    		map.put(set, keyword);
    	}
	}
	
	
    

}
