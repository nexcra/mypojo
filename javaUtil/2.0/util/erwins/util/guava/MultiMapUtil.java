package erwins.util.guava;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import erwins.util.root.KeyValueVo;

public abstract class MultiMapUtil {
	
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

}
