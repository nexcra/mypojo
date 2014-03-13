package erwins.util.collections;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import erwins.util.root.NotThreadSafe;
import erwins.util.spring.SpringUtil;

/** 
 * LikeStringMatchMap
 *   */
@NotThreadSafe
public class LikeStringMatchSet extends AbstractSetSupport<String>{
	
	private Set<String> set = Sets.newHashSet();
	private int minLength = 2;

	@Override
	public boolean add(String e) {
		return set.add(e);
	}
	
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
