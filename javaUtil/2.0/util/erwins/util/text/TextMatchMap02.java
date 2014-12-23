package erwins.util.text;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import erwins.util.lib.CollectionUtil;


/** 
 * 텍스트분석 샘플저장
 * 오타를 찾아낸다. seed에 있는 입력값은 무시한다.
 * 길이/순서 같고, 다 같은 문자인데 딱 한글자만 다름. 이 다른 한글자는 형태소 순서,숫자도 같고 단 1개만 틀려야함.  
 * ex) 오리닭집 -> 오리닰집
 * */
public class TextMatchMap02{

	private List<Multimap<Character,String>> seedREpository = Lists.newArrayList();
	private Set<String> unique = Sets.newHashSet();
	private static final int MAX_WARD = 100;
	
	public TextMatchMap02(Collection<String> list){
		
    	for(int i=0;i<MAX_WARD;i++){
    		Multimap<Character,String> map = HashMultimap.create();
    		seedREpository.add(map);
    	}
    	
    	for(String keyword : list){
    		unique.add(keyword);
    		for(int i=0;i<keyword.length();i++){
        		Multimap<Character,String> map = seedREpository.get(i);
    			char key = keyword.charAt(i);
    			map.put(key, keyword);
    		}
    	}
	}

    public List<String> parse(String input){
    	
    	if(input.length() > seedREpository.size()) throw new IllegalArgumentException("키워드의 길이가 너무 길어요. 최대 " + MAX_WARD);
    	
    	List<String> result = Lists.newArrayList();
    	if(unique.contains(input)) return result; //SEED에 있는 쿼리는 스킵된다.
    	
    	Set<Character> inputSet = StringUtil.toCharSet(input); 
    	
    	//전부 카운터에 넣어서 몇개나 매치되는지 알아본다.
    	Multiset<String> counter = HashMultiset.create();
    	for(int i=0;i<input.length();i++){
    		Multimap<Character,String> map = seedREpository.get(i);
			char key = input.charAt(i);
			Collection<String> set = map.get(key);
			for(String each : set){
				if(input.length() != each.length()) continue; //길이가 동일해야 비교대상이 된다.
				counter.add(each);	
			}
		}
    	
    	for(String matched : counter.elementSet()){
    		//1개 문자만 틀린거라면
    		if(counter.count(matched) == input.length() -1){
    			Set<Character> matchSet = StringUtil.toCharSet(matched); 
    			Collection<?> diff = CollectionUtil.disjunction(inputSet, matchSet);
    			if(diff.size() != 2) continue; //틀린단어 2개를 찾아낸다.
    			
    			//형태소 분석
    			Iterator<?> it = diff.iterator(); 
    			String alpha1 = HanUtil.hanToAlpha(it.next().toString());
    			String alpha2 = HanUtil.hanToAlpha(it.next().toString());
    			
    			if(alpha1.length() != alpha2.length()) continue; //형태소 순서,숫자도 같고 단 1개만 틀려야함.
    			
    			Set<Character> set1 = StringUtil.toCharSet(alpha1);
    			Set<Character> set2 = StringUtil.toCharSet(alpha2);
    			
    			if(CollectionUtil.disjunction(set1, set2).size()==2){
    				result.add(matched);
    			}
    		}
    	}
    	return result;
    	
    }

}
