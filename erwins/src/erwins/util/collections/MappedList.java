package erwins.util.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 이하의 간단 버전이다.
 * private List<Map<String,T>> list = new ArrayList<Map<String,T>>();
 * ????????? 쓸모없어 보인다. ㅠㅠ
 */

public class MappedList<T> implements Iterable<Map<String,T>>{
	
	private List<Map<String,T>> list = new ArrayList<Map<String,T>>();

	@Override
	public Iterator<Map<String, T>> iterator() {
		return list.iterator();
	}
    
}