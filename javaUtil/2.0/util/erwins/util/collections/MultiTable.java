package erwins.util.collections;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/** Table 흉내낸 짝퉁. 절대 스래드 안전하지 않으니 주의!! */
public class MultiTable<R,C,V> implements Iterable<Entry<R, Multimap<C, V>>>{
	
	private Map<R,Multimap<C,V>> table = Maps.newHashMap();
	
	public boolean put(R r,C c,V v){
		Multimap<C,V> multiMap = table.get(r);
		if(multiMap==null){
			multiMap = ArrayListMultimap.create();
			table.put(r, multiMap);
		}
		return multiMap.put(c, v);
	}
	@Override
	public Iterator<Entry<R, Multimap<C, V>>> iterator() {
		return table.entrySet().iterator();
	}
    

    

}
