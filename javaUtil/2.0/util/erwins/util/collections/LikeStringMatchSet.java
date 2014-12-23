package erwins.util.collections;

import java.util.List;
import java.util.Set;

import lombok.experimental.Delegate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.SpringUtil;

/** 
 *  키워드들(짧은 단어)를 메모리에 미리 넣어놓고, 단어들(긴 단어)을 매칭할때 사용된다.
 *   
 *   */
@NotThreadSafe
public class LikeStringMatchSet implements Set<String>{
	
	@Delegate
	private Set<String> set = Sets.newHashSet();
	private int minLength = 2;

	/** 
	 * 10자 텍스트의 경우 minLength=1 이라면 set에서 55번의 검색을 하게된다.
	 *  즉 10+9+8+7+6+5+4+3+2+1 = 55
	 *    */
	public List<String> matchAny(String query){
		List<String> result = Lists.newArrayList();
		for(String subText : SpringUtil.splitWord(query,minLength)){
			if(!set.contains(subText)) continue;
			result.add(subText);
		}
		return result;
	}

	public int getMinLength() {
		return minLength;
	}
	
	public LikeStringMatchSet setMinLength(int minLength) {
		this.minLength = minLength;
		return this;
	}
	


}
